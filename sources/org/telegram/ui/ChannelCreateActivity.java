package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChannelCreateActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
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
    public ImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private boolean canCreatePublic = true;
    /* access modifiers changed from: private */
    public int chatId;
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
    private TLRPC.ExportedChatInvite invite;
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
    private TextBlockCell privateContainer;
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private TLRPC.InputFile uploadedAvatar;

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChannelCreateActivity(Bundle bundle) {
        super(bundle);
        this.currentStep = bundle.getInt("step", 0);
        int i = this.currentStep;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater();
            TLRPC.TL_channels_checkUsername tL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
            tL_channels_checkUsername.username = "1";
            tL_channels_checkUsername.channel = new TLRPC.TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$new$1$ChannelCreateActivity(tLObject, tL_error);
                }
            });
            return;
        }
        if (i == 1) {
            this.canCreatePublic = bundle.getBoolean("canCreatePublic", true);
            boolean z = this.canCreatePublic;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = bundle.getInt("chat_id", 0);
    }

    public /* synthetic */ void lambda$new$1$ChannelCreateActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$0$ChannelCreateActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ChannelCreateActivity(TLRPC.TL_error tL_error) {
        this.canCreatePublic = tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
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
            imageUpdater2.delegate = this;
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
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
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
            public void onItemClick(int i) {
                if (i == -1) {
                    ChannelCreateActivity.this.finishFragment();
                } else if (i != 1) {
                } else {
                    if (ChannelCreateActivity.this.currentStep == 0) {
                        if (!ChannelCreateActivity.this.donePressed && ChannelCreateActivity.this.getParentActivity() != null) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                Vibrator vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                                return;
                            }
                            boolean unused = ChannelCreateActivity.this.donePressed = true;
                            if (ChannelCreateActivity.this.imageUpdater.uploadingImage != null) {
                                boolean unused2 = ChannelCreateActivity.this.createAfterUpload = true;
                                ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                                AlertDialog unused3 = channelCreateActivity.progressDialog = new AlertDialog(channelCreateActivity.getParentActivity(), 3);
                                ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public final void onCancel(DialogInterface dialogInterface) {
                                        ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$0$ChannelCreateActivity$1(dialogInterface);
                                    }
                                });
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            int createChat = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, (Location) null, (String) null, ChannelCreateActivity.this);
                            ChannelCreateActivity channelCreateActivity2 = ChannelCreateActivity.this;
                            AlertDialog unused4 = channelCreateActivity2.progressDialog = new AlertDialog(channelCreateActivity2.getParentActivity(), 3);
                            ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(createChat) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onCancel(DialogInterface dialogInterface) {
                                    ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$1$ChannelCreateActivity$1(this.f$1, dialogInterface);
                                }
                            });
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
                                Vibrator vibrator2 = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (vibrator2 != null) {
                                    vibrator2.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                return;
                            } else {
                                MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", 2);
                        bundle.putInt("chatId", ChannelCreateActivity.this.chatId);
                        bundle.putInt("chatType", 2);
                        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(bundle), true);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ChannelCreateActivity$1(DialogInterface dialogInterface) {
                boolean unused = ChannelCreateActivity.this.createAfterUpload = false;
                AlertDialog unused2 = ChannelCreateActivity.this.progressDialog = null;
                boolean unused3 = ChannelCreateActivity.this.donePressed = false;
            }

            public /* synthetic */ void lambda$onItemClick$1$ChannelCreateActivity$1(int i, DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(i, true);
                boolean unused = ChannelCreateActivity.this.donePressed = false;
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        int i = this.currentStep;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", NUM));
            AnonymousClass2 r2 = new SizeNotifierFrameLayout(context2, SharedConfig.smoothKeyboard) {
                private boolean ignoreLayout;

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    int paddingTop = size2 - getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, i, 0, i2, 0);
                    if ((SharedConfig.smoothKeyboard ? 0 : getKeyboardHeight()) > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = getChildAt(i3);
                        if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChannelCreateActivity.this.actionBar)) {
                            if (ChannelCreateActivity.this.nameTextView == null || !ChannelCreateActivity.this.nameTextView.isPopupView(childAt)) {
                                measureChildWithMargins(childAt, i, 0, i2, 0);
                            } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        }
                    }
                }

                /* access modifiers changed from: protected */
                /* JADX WARNING: Removed duplicated region for block: B:29:0x0078  */
                /* JADX WARNING: Removed duplicated region for block: B:36:0x0092  */
                /* JADX WARNING: Removed duplicated region for block: B:44:0x00b9  */
                /* JADX WARNING: Removed duplicated region for block: B:45:0x00c2  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                    /*
                        r10 = this;
                        int r11 = r10.getChildCount()
                        boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
                        r1 = 0
                        if (r0 == 0) goto L_0x000b
                        r0 = 0
                        goto L_0x000f
                    L_0x000b:
                        int r0 = r10.getKeyboardHeight()
                    L_0x000f:
                        r2 = 1101004800(0x41a00000, float:20.0)
                        int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                        if (r0 > r2) goto L_0x002c
                        boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                        if (r2 != 0) goto L_0x002c
                        boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                        if (r2 != 0) goto L_0x002c
                        org.telegram.ui.ChannelCreateActivity r2 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r2 = r2.nameTextView
                        int r2 = r2.getEmojiPadding()
                        goto L_0x002d
                    L_0x002c:
                        r2 = 0
                    L_0x002d:
                        r10.setBottomClip(r2)
                    L_0x0030:
                        if (r1 >= r11) goto L_0x00d5
                        android.view.View r3 = r10.getChildAt(r1)
                        int r4 = r3.getVisibility()
                        r5 = 8
                        if (r4 != r5) goto L_0x0040
                        goto L_0x00d1
                    L_0x0040:
                        android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
                        android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                        int r5 = r3.getMeasuredWidth()
                        int r6 = r3.getMeasuredHeight()
                        int r7 = r4.gravity
                        r8 = -1
                        if (r7 != r8) goto L_0x0055
                        r7 = 51
                    L_0x0055:
                        r8 = r7 & 7
                        r7 = r7 & 112(0x70, float:1.57E-43)
                        r8 = r8 & 7
                        r9 = 1
                        if (r8 == r9) goto L_0x0069
                        r9 = 5
                        if (r8 == r9) goto L_0x0064
                        int r8 = r4.leftMargin
                        goto L_0x0074
                    L_0x0064:
                        int r8 = r14 - r5
                        int r9 = r4.rightMargin
                        goto L_0x0073
                    L_0x0069:
                        int r8 = r14 - r12
                        int r8 = r8 - r5
                        int r8 = r8 / 2
                        int r9 = r4.leftMargin
                        int r8 = r8 + r9
                        int r9 = r4.rightMargin
                    L_0x0073:
                        int r8 = r8 - r9
                    L_0x0074:
                        r9 = 16
                        if (r7 == r9) goto L_0x0092
                        r9 = 48
                        if (r7 == r9) goto L_0x008a
                        r9 = 80
                        if (r7 == r9) goto L_0x0083
                        int r4 = r4.topMargin
                        goto L_0x009f
                    L_0x0083:
                        int r7 = r15 - r2
                        int r7 = r7 - r13
                        int r7 = r7 - r6
                        int r4 = r4.bottomMargin
                        goto L_0x009d
                    L_0x008a:
                        int r4 = r4.topMargin
                        int r7 = r10.getPaddingTop()
                        int r4 = r4 + r7
                        goto L_0x009f
                    L_0x0092:
                        int r7 = r15 - r2
                        int r7 = r7 - r13
                        int r7 = r7 - r6
                        int r7 = r7 / 2
                        int r9 = r4.topMargin
                        int r7 = r7 + r9
                        int r4 = r4.bottomMargin
                    L_0x009d:
                        int r4 = r7 - r4
                    L_0x009f:
                        org.telegram.ui.ChannelCreateActivity r7 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                        if (r7 == 0) goto L_0x00cc
                        org.telegram.ui.ChannelCreateActivity r7 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                        boolean r7 = r7.isPopupView(r3)
                        if (r7 == 0) goto L_0x00cc
                        boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                        if (r4 == 0) goto L_0x00c2
                        int r4 = r10.getMeasuredHeight()
                        int r7 = r3.getMeasuredHeight()
                        goto L_0x00cb
                    L_0x00c2:
                        int r4 = r10.getMeasuredHeight()
                        int r4 = r4 + r0
                        int r7 = r3.getMeasuredHeight()
                    L_0x00cb:
                        int r4 = r4 - r7
                    L_0x00cc:
                        int r5 = r5 + r8
                        int r6 = r6 + r4
                        r3.layout(r8, r4, r5, r6)
                    L_0x00d1:
                        int r1 = r1 + 1
                        goto L_0x0030
                    L_0x00d5:
                        r10.notifyHeightChanged()
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelCreateActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            r2.setOnTouchListener($$Lambda$ChannelCreateActivity$lIiXta2UxN2m1ViR2Lzg4Rdjg4.INSTANCE);
            this.fragmentView = r2;
            this.fragmentView.setTag("windowBackgroundWhite");
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout = new LinearLayout(context2);
            this.linearLayout.setOrientation(1);
            r2.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.avatarImage = new BackupImageView(context2) {
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int i, int i2, int i3, int i4) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$4$ChannelCreateActivity(view);
                }
            });
            this.avatarEditor = new ImageView(context2) {
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarProgressView = new RadialProgressView(context2);
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            this.nameTextView = new EditTextEmoji(context2, r2, this, 0);
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
            String str = this.nameToSet;
            if (str != null) {
                this.nameTextView.setText(str);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
            this.descriptionTextView = new EditTextBoldCursor(context2);
            this.descriptionTextView.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.descriptionTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", NUM));
            this.descriptionTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ChannelCreateActivity.this.lambda$createView$5$ChannelCreateActivity(textView, i, keyEvent);
                }
            });
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
            this.helpTextView = new TextView(context2);
            this.helpTextView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", NUM));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i == 1) {
            this.fragmentView = new ScrollView(context2);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            this.linearLayout = new LinearLayout(context2);
            this.linearLayout.setOrientation(1);
            scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", NUM));
            this.fragmentView.setTag("windowBackgroundGray");
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.headerCell2 = new HeaderCell(context2, 23);
            this.headerCell2.setHeight(46);
            this.headerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", NUM));
            this.linearLayout.addView(this.headerCell2);
            this.linearLayout2 = new LinearLayout(context2);
            this.linearLayout2.setOrientation(1);
            this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1 = new RadioButtonCell(context2);
            this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", NUM), LocaleController.getString("ChannelPublicInfo", NUM), false, !this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$6$ChannelCreateActivity(view);
                }
            });
            this.radioButtonCell2 = new RadioButtonCell(context2);
            this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), false, this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$7$ChannelCreateActivity(view);
                }
            });
            this.sectionCell = new ShadowSectionCell(context2);
            this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
            this.linkContainer = new LinearLayout(context2);
            this.linkContainer.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            this.headerCell = new HeaderCell(context2);
            this.linkContainer.addView(this.headerCell);
            this.publicContainer = new LinearLayout(context2);
            this.publicContainer.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            this.editText = new EditTextBoldCursor(context2);
            EditTextBoldCursor editTextBoldCursor = this.editText;
            editTextBoldCursor.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
            this.editText.setTextSize(1, 18.0f);
            this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable((Drawable) null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            this.descriptionTextView = new EditTextBoldCursor(context2);
            this.descriptionTextView.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.descriptionTextView.setMaxLines(1);
            this.descriptionTextView.setLines(1);
            this.descriptionTextView.setBackgroundDrawable((Drawable) null);
            this.descriptionTextView.setPadding(0, 0, 0, 0);
            this.descriptionTextView.setSingleLine(true);
            this.descriptionTextView.setInputType(163872);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
            this.descriptionTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    boolean unused = channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                }
            });
            this.privateContainer = new TextBlockCell(context2);
            this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$8$ChannelCreateActivity(view);
                }
            });
            this.checkTextView = new TextView(context2);
            this.checkTextView.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            this.typeInfoCell = new TextInfoPrivacyCell(context2);
            this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            this.loadingAdminedCell = new LoadingCell(context2);
            this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
            this.adminnedChannelsLayout = new LinearLayout(context2);
            this.adminnedChannelsLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            this.adminedInfoCell = new TextInfoPrivacyCell(context2);
            this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$ChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChannelCreateActivity.this.lambda$null$3$ChannelCreateActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$3$ChannelCreateActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
    }

    public /* synthetic */ boolean lambda$createView$5$ChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$6$ChannelCreateActivity(View view) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$7$ChannelCreateActivity(View view) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$8$ChannelCreateActivity(View view) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TLRPC.TL_messages_exportChatInvite tL_messages_exportChatInvite = new TLRPC.TL_messages_exportChatInvite();
            tL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_exportChatInvite, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$generateLink$10$ChannelCreateActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$generateLink$10$ChannelCreateActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$9$ChannelCreateActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$ChannelCreateActivity(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            this.invite = (TLRPC.ExportedChatInvite) tLObject;
        }
        this.loadingInvite = false;
        TextBlockCell textBlockCell = this.privateContainer;
        TLRPC.ExportedChatInvite exportedChatInvite = this.invite;
        textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", NUM), false);
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
                TextBlockCell textBlockCell = this.privateContainer;
                TLRPC.ExportedChatInvite exportedChatInvite = this.invite;
                textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", NUM), false);
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

    public void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable(inputFile, photoSize2, photoSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$didUploadPhoto$11$ChannelCreateActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$11$ChannelCreateActivity(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
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
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChannelCreateActivity.this.avatarAnimation != null && ChannelCreateActivity.this.avatarEditor != null) {
                            if (z) {
                                ChannelCreateActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChannelCreateActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
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

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onActivityResult(i, i2, intent);
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        String obj;
        String str;
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (!(imageUpdater2 == null || (str = imageUpdater2.currentPicturePath) == null)) {
                bundle.putString("path", str);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null && (obj = editTextEmoji.getText().toString()) != null && obj.length() != 0) {
                bundle.putString("nameTextView", obj);
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (imageUpdater2 != null) {
                imageUpdater2.currentPicturePath = bundle.getString("path");
            }
            String string = bundle.getString("nameTextView");
            if (string != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(string);
                } else {
                    this.nameToSet = string;
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.currentStep != 1) {
            this.nameTextView.requestFocus();
            this.nameTextView.openKeyboard();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
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
            int intValue = objArr[0].intValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putInt("chat_id", intValue);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(intValue, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$17$ChannelCreateActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadAdminedChannels$17$ChannelCreateActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$16$ChannelCreateActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ChannelCreateActivity(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject != null && getParentActivity() != null) {
            for (int i = 0; i < this.adminedChannelCells.size(); i++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(i));
            }
            this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
            for (int i2 = 0; i2 < tL_messages_chats.chats.size(); i2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChannelCreateActivity.this.lambda$null$15$ChannelCreateActivity(view);
                    }
                });
                TLRPC.Chat chat = tL_messages_chats.chats.get(i2);
                boolean z = true;
                if (i2 != tL_messages_chats.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$null$15$ChannelCreateActivity(View view) {
        TLRPC.Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (currentChannel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener(currentChannel) {
            private final /* synthetic */ TLRPC.Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.lambda$null$14$ChannelCreateActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$14$ChannelCreateActivity(TLRPC.Chat chat, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername tL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
        tL_channels_updateUsername.channel = MessagesController.getInputChannel(chat);
        tL_channels_updateUsername.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updateUsername, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$null$13$ChannelCreateActivity(tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$null$13$ChannelCreateActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChannelCreateActivity.this.lambda$null$12$ChannelCreateActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$ChannelCreateActivity() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String str) {
        if (str == null || str.length() <= 0) {
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
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
                    this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                    this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    return false;
                } else {
                    i++;
                }
            }
        }
        if (str == null || str.length() < 5) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteRedText4");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            return false;
        } else if (str.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteRedText4");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.lastCheckName = str;
            this.checkRunnable = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelCreateActivity.this.lambda$checkUserName$20$ChannelCreateActivity(this.f$1);
                }
            };
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$20$ChannelCreateActivity(String str) {
        TLRPC.TL_channels_checkUsername tL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        tL_channels_checkUsername.username = str;
        tL_channels_checkUsername.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new RequestDelegate(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$null$19$ChannelCreateActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$19$ChannelCreateActivity(String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tL_error, tLObject) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$18$ChannelCreateActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$18$ChannelCreateActivity(String str, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tL_error != null || !(tLObject instanceof TLRPC.TL_boolTrue)) {
                if (tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
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
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, str));
            this.checkTextView.setTag("windowBackgroundWhiteGreenText");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    private void showErrorAlert(String str) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != NUM) {
                if (hashCode == NUM && str.equals("USERNAME_OCCUPIED")) {
                    c = 1;
                }
            } else if (str.equals("USERNAME_INVALID")) {
                c = 0;
            }
            if (c == 0) {
                builder.setMessage(LocaleController.getString("LinkInvalid", NUM));
            } else if (c != 1) {
                builder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
            } else {
                builder.setMessage(LocaleController.getString("LinkInUse", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ChannelCreateActivity$ieYbFS24yeOuvItbU135YWldd4c r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChannelCreateActivity.this.lambda$getThemeDescriptions$21$ChannelCreateActivity();
            }
        };
        $$Lambda$ChannelCreateActivity$ieYbFS24yeOuvItbU135YWldd4c r7 = r9;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, r7, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$21$ChannelCreateActivity() {
        LinearLayout linearLayout3 = this.adminnedChannelsLayout;
        if (linearLayout3 != null) {
            int childCount = linearLayout3.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.adminnedChannelsLayout.getChildAt(i);
                if (childAt instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) childAt).update();
                }
            }
        }
    }
}
