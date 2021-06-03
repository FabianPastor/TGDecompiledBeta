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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC$TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvites;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.ChannelCreateActivity;
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
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private TLRPC$FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC$FileLocation avatarBig;
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
    private TLRPC$InputFile inputPhoto;
    private TLRPC$InputFile inputVideo;
    private String inputVideoPath;
    private TLRPC$TL_chatInviteExported invite;
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

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChannelCreateActivity(Bundle bundle) {
        super(bundle);
        int i = bundle.getInt("step", 0);
        this.currentStep = i;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater(true);
            TLRPC$TL_channels_checkUsername tLRPC$TL_channels_checkUsername = new TLRPC$TL_channels_checkUsername();
            tLRPC$TL_channels_checkUsername.username = "1";
            tLRPC$TL_channels_checkUsername.channel = new TLRPC$TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_checkUsername, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChannelCreateActivity.this.lambda$new$1$ChannelCreateActivity(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        if (i == 1) {
            boolean z = bundle.getBoolean("canCreatePublic", true);
            this.canCreatePublic = z;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = bundle.getInt("chat_id", 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ChannelCreateActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.canCreatePublic = tLRPC$TL_error == null || !tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ChannelCreateActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$new$0$ChannelCreateActivity(this.f$1);
            }
        });
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
            imageUpdater2.setDelegate(this);
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

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onRequestPermissionsResultFragment(i, strArr, iArr);
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
                            if (ChannelCreateActivity.this.imageUpdater.isUploadingImage()) {
                                boolean unused2 = ChannelCreateActivity.this.createAfterUpload = true;
                                AlertDialog unused3 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                                ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public final void onCancel(DialogInterface dialogInterface) {
                                        ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$0$ChannelCreateActivity$1(dialogInterface);
                                    }
                                });
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            int createChat = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, false, (Location) null, (String) null, ChannelCreateActivity.this);
                            AlertDialog unused4 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                            ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(createChat) {
                                public final /* synthetic */ int f$1;

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

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$0 */
            public /* synthetic */ void lambda$onItemClick$0$ChannelCreateActivity$1(DialogInterface dialogInterface) {
                boolean unused = ChannelCreateActivity.this.createAfterUpload = false;
                AlertDialog unused2 = ChannelCreateActivity.this.progressDialog = null;
                boolean unused3 = ChannelCreateActivity.this.donePressed = false;
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$1 */
            public /* synthetic */ void lambda$onItemClick$1$ChannelCreateActivity$1(int i, DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(i, true);
                boolean unused = ChannelCreateActivity.this.donePressed = false;
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        int i = this.currentStep;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", NUM));
            AnonymousClass2 r0 = new SizeNotifierFrameLayout(context2) {
                private boolean ignoreLayout;

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    int paddingTop = size2 - getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, i, 0, i2, 0);
                    if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
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
                /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
                /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
                /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
                /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                    /*
                        r10 = this;
                        int r11 = r10.getChildCount()
                        int r0 = r10.measureKeyboardHeight()
                        r1 = 1101004800(0x41a00000, float:20.0)
                        int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                        r2 = 0
                        if (r0 > r1) goto L_0x0026
                        boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                        if (r1 != 0) goto L_0x0026
                        boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                        if (r1 != 0) goto L_0x0026
                        org.telegram.ui.ChannelCreateActivity r1 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r1 = r1.nameTextView
                        int r1 = r1.getEmojiPadding()
                        goto L_0x0027
                    L_0x0026:
                        r1 = 0
                    L_0x0027:
                        r10.setBottomClip(r1)
                    L_0x002a:
                        if (r2 >= r11) goto L_0x00cf
                        android.view.View r3 = r10.getChildAt(r2)
                        int r4 = r3.getVisibility()
                        r5 = 8
                        if (r4 != r5) goto L_0x003a
                        goto L_0x00cb
                    L_0x003a:
                        android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
                        android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                        int r5 = r3.getMeasuredWidth()
                        int r6 = r3.getMeasuredHeight()
                        int r7 = r4.gravity
                        r8 = -1
                        if (r7 != r8) goto L_0x004f
                        r7 = 51
                    L_0x004f:
                        r8 = r7 & 7
                        r7 = r7 & 112(0x70, float:1.57E-43)
                        r8 = r8 & 7
                        r9 = 1
                        if (r8 == r9) goto L_0x0063
                        r9 = 5
                        if (r8 == r9) goto L_0x005e
                        int r8 = r4.leftMargin
                        goto L_0x006e
                    L_0x005e:
                        int r8 = r14 - r5
                        int r9 = r4.rightMargin
                        goto L_0x006d
                    L_0x0063:
                        int r8 = r14 - r12
                        int r8 = r8 - r5
                        int r8 = r8 / 2
                        int r9 = r4.leftMargin
                        int r8 = r8 + r9
                        int r9 = r4.rightMargin
                    L_0x006d:
                        int r8 = r8 - r9
                    L_0x006e:
                        r9 = 16
                        if (r7 == r9) goto L_0x008c
                        r9 = 48
                        if (r7 == r9) goto L_0x0084
                        r9 = 80
                        if (r7 == r9) goto L_0x007d
                        int r4 = r4.topMargin
                        goto L_0x0099
                    L_0x007d:
                        int r7 = r15 - r1
                        int r7 = r7 - r13
                        int r7 = r7 - r6
                        int r4 = r4.bottomMargin
                        goto L_0x0097
                    L_0x0084:
                        int r4 = r4.topMargin
                        int r7 = r10.getPaddingTop()
                        int r4 = r4 + r7
                        goto L_0x0099
                    L_0x008c:
                        int r7 = r15 - r1
                        int r7 = r7 - r13
                        int r7 = r7 - r6
                        int r7 = r7 / 2
                        int r9 = r4.topMargin
                        int r7 = r7 + r9
                        int r4 = r4.bottomMargin
                    L_0x0097:
                        int r4 = r7 - r4
                    L_0x0099:
                        org.telegram.ui.ChannelCreateActivity r7 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                        if (r7 == 0) goto L_0x00c6
                        org.telegram.ui.ChannelCreateActivity r7 = org.telegram.ui.ChannelCreateActivity.this
                        org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                        boolean r7 = r7.isPopupView(r3)
                        if (r7 == 0) goto L_0x00c6
                        boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                        if (r4 == 0) goto L_0x00bc
                        int r4 = r10.getMeasuredHeight()
                        int r7 = r3.getMeasuredHeight()
                        goto L_0x00c5
                    L_0x00bc:
                        int r4 = r10.getMeasuredHeight()
                        int r4 = r4 + r0
                        int r7 = r3.getMeasuredHeight()
                    L_0x00c5:
                        int r4 = r4 - r7
                    L_0x00c6:
                        int r5 = r5 + r8
                        int r6 = r6 + r4
                        r3.layout(r8, r4, r5, r6)
                    L_0x00cb:
                        int r2 = r2 + 1
                        goto L_0x002a
                    L_0x00cf:
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
            r0.setOnTouchListener($$Lambda$ChannelCreateActivity$VLyc6f8PPzmYLCvcLefQkXWZMU.INSTANCE);
            this.fragmentView = r0;
            r0.setTag("windowBackgroundWhite");
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            LinearLayout linearLayout3 = new LinearLayout(context2);
            this.linearLayout = linearLayout3;
            linearLayout3.setOrientation(1);
            r0.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            AnonymousClass3 r2 = new BackupImageView(context2) {
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
            this.avatarImage = r2;
            r2.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            BackupImageView backupImageView = this.avatarImage;
            boolean z = LocaleController.isRTL;
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(64, 64.0f, (z ? 5 : 3) | 48, z ? 0.0f : 16.0f, 12.0f, z ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            AnonymousClass4 r12 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = r12;
            boolean z2 = LocaleController.isRTL;
            frameLayout.addView(r12, LayoutHelper.createFrame(64, 64.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 16.0f, 12.0f, z2 ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$5$ChannelCreateActivity(view);
                }
            });
            this.cameraDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, (int[]) null);
            AnonymousClass5 r22 = new RLottieImageView(context2) {
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = r22;
            r22.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(1.0f));
            RLottieImageView rLottieImageView = this.avatarEditor;
            boolean z3 = LocaleController.isRTL;
            frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(64, 64.0f, (z3 ? 5 : 3) | 48, z3 ? 0.0f : 16.0f, 12.0f, z3 ? 16.0f : 0.0f, 12.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            RadialProgressView radialProgressView2 = this.avatarProgressView;
            boolean z4 = LocaleController.isRTL;
            frameLayout.addView(radialProgressView2, LayoutHelper.createFrame(64, 64.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : 16.0f, 12.0f, z4 ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, r0, this, 0);
            this.nameTextView = editTextEmoji2;
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", NUM));
            String str = this.nameToSet;
            if (str != null) {
                this.nameTextView.setText(str);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            this.nameTextView.getEditText().setSingleLine(true);
            this.nameTextView.getEditText().setImeOptions(5);
            this.nameTextView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ChannelCreateActivity.this.lambda$createView$6$ChannelCreateActivity(textView, i, keyEvent);
                }
            });
            EditTextEmoji editTextEmoji3 = this.nameTextView;
            boolean z5 = LocaleController.isRTL;
            frameLayout.addView(editTextEmoji3, LayoutHelper.createFrame(-1, -2.0f, 16, z5 ? 5.0f : 96.0f, 0.0f, z5 ? 96.0f : 5.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.descriptionTextView = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 18.0f);
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
                    return ChannelCreateActivity.this.lambda$createView$7$ChannelCreateActivity(textView, i, keyEvent);
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
            TextView textView = new TextView(context2);
            this.helpTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", NUM));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i == 1) {
            ScrollView scrollView = new ScrollView(context2);
            this.fragmentView = scrollView;
            ScrollView scrollView2 = scrollView;
            scrollView2.setFillViewport(true);
            LinearLayout linearLayout4 = new LinearLayout(context2);
            this.linearLayout = linearLayout4;
            linearLayout4.setOrientation(1);
            scrollView2.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
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
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$8$ChannelCreateActivity(view);
                }
            });
            RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
            this.radioButtonCell2 = radioButtonCell3;
            radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), false, this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$9$ChannelCreateActivity(view);
                }
            });
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
            EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context2);
            this.descriptionTextView = editTextBoldCursor3;
            editTextBoldCursor3.setTextSize(1, 18.0f);
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
            LinearLayout linearLayout8 = new LinearLayout(context2);
            this.privateContainer = linearLayout8;
            linearLayout8.setOrientation(1);
            this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
            LinkActionView linkActionView = new LinkActionView(context, this, (BottomSheet) null, this.chatId, true, ChatObject.isChannel(getMessagesController().getChat(Integer.valueOf(this.chatId))));
            this.permanentLinkView = linkActionView;
            linkActionView.hideRevokeOption(true);
            this.permanentLinkView.setUsers(0, (ArrayList<TLRPC$User>) null);
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
            linearLayout9.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.adminedInfoCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChannelCreateActivity.this.lambda$createView$3$ChannelCreateActivity();
            }
        }, new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                ChannelCreateActivity.this.lambda$createView$4$ChannelCreateActivity(dialogInterface);
            }
        });
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.avatarEditor.playAnimation();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ChannelCreateActivity() {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$ChannelCreateActivity(DialogInterface dialogInterface) {
        this.cameraDrawable.setCustomEndFrame(86);
        this.avatarEditor.playAnimation();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ boolean lambda$createView$6$ChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 || TextUtils.isEmpty(this.nameTextView.getEditText().getText())) {
            return false;
        }
        this.descriptionTextView.requestFocus();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ boolean lambda$createView$7$ChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ChannelCreateActivity(View view) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ChannelCreateActivity(View view) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            TLRPC$ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
            if (chatFull != null) {
                this.invite = chatFull.exported_invite;
            }
            if (this.invite == null) {
                this.loadingInvite = true;
                TLRPC$TL_messages_getExportedChatInvites tLRPC$TL_messages_getExportedChatInvites = new TLRPC$TL_messages_getExportedChatInvites();
                tLRPC$TL_messages_getExportedChatInvites.peer = getMessagesController().getInputPeer(-this.chatId);
                tLRPC$TL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
                tLRPC$TL_messages_getExportedChatInvites.limit = 1;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getExportedChatInvites, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ChannelCreateActivity.this.lambda$generateLink$11$ChannelCreateActivity(tLObject, tLRPC$TL_error);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$11 */
    public /* synthetic */ void lambda$generateLink$11$ChannelCreateActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$generateLink$10$ChannelCreateActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$10 */
    public /* synthetic */ void lambda$generateLink$10$ChannelCreateActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$TL_chatInviteExported) ((TLRPC$TL_messages_exportedChatInvites) tLObject).invites.get(0);
        }
        this.loadingInvite = false;
        LinkActionView linkActionView = this.permanentLinkView;
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
        linkActionView.setLink(tLRPC$TL_chatInviteExported != null ? tLRPC$TL_chatInviteExported.link : null);
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
                TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
                linkActionView.setLink(tLRPC$TL_chatInviteExported != null ? tLRPC$TL_chatInviteExported.link : null);
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

    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(f);
        }
    }

    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$InputFile, tLRPC$InputFile2, str, d, tLRPC$PhotoSize2, tLRPC$PhotoSize) {
            public final /* synthetic */ TLRPC$InputFile f$1;
            public final /* synthetic */ TLRPC$InputFile f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ double f$4;
            public final /* synthetic */ TLRPC$PhotoSize f$5;
            public final /* synthetic */ TLRPC$PhotoSize f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$didUploadPhoto$12$ChannelCreateActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didUploadPhoto$12 */
    public /* synthetic */ void lambda$didUploadPhoto$12$ChannelCreateActivity(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, String str, double d, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        if (tLRPC$InputFile == null && tLRPC$InputFile2 == null) {
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            this.avatar = tLRPC$FileLocation;
            this.avatarBig = tLRPC$PhotoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) null);
            showAvatarProgress(true, false);
            return;
        }
        this.inputPhoto = tLRPC$InputFile;
        this.inputVideo = tLRPC$InputFile2;
        this.inputVideoPath = str;
        this.videoTimestamp = d;
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
        String str;
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (!(imageUpdater2 == null || (str = imageUpdater2.currentPicturePath) == null)) {
                bundle.putString("path", str);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null) {
                String obj = editTextEmoji.getText().toString();
                if (obj.length() != 0) {
                    bundle.putString("nameTextView", obj);
                }
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
            if (!(this.inputPhoto == null && this.inputVideo == null)) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(intValue, (TLRPC$TL_inputChatPhoto) null, this.inputPhoto, this.inputVideo, this.videoTimestamp, this.inputVideoPath, this.avatar, this.avatarBig, (Runnable) null);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$18$ChannelCreateActivity(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$18 */
    public /* synthetic */ void lambda$loadAdminedChannels$18$ChannelCreateActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$17$ChannelCreateActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$17 */
    public /* synthetic */ void lambda$loadAdminedChannels$17$ChannelCreateActivity(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject != null && getParentActivity() != null) {
            for (int i = 0; i < this.adminedChannelCells.size(); i++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(i));
            }
            this.adminedChannelCells.clear();
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            for (int i2 = 0; i2 < tLRPC$TL_messages_chats.chats.size(); i2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChannelCreateActivity.this.lambda$loadAdminedChannels$16$ChannelCreateActivity(view);
                    }
                });
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_chats.chats.get(i2);
                boolean z = true;
                if (i2 != tLRPC$TL_messages_chats.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(tLRPC$Chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$16 */
    public /* synthetic */ void lambda$loadAdminedChannels$16$ChannelCreateActivity(View view) {
        TLRPC$Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (currentChannel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener(currentChannel) {
            public final /* synthetic */ TLRPC$Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$15$ChannelCreateActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$15 */
    public /* synthetic */ void lambda$loadAdminedChannels$15$ChannelCreateActivity(TLRPC$Chat tLRPC$Chat, DialogInterface dialogInterface, int i) {
        TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername = new TLRPC$TL_channels_updateUsername();
        tLRPC$TL_channels_updateUsername.channel = MessagesController.getInputChannel(tLRPC$Chat);
        tLRPC$TL_channels_updateUsername.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_updateUsername, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$14$ChannelCreateActivity(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$14 */
    public /* synthetic */ void lambda$loadAdminedChannels$14$ChannelCreateActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$13$ChannelCreateActivity();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$13 */
    public /* synthetic */ void lambda$loadAdminedChannels$13$ChannelCreateActivity() {
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
            $$Lambda$ChannelCreateActivity$QTEKnlRx7DFsCWMsSfdInRu7TQ r0 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelCreateActivity.this.lambda$checkUserName$21$ChannelCreateActivity(this.f$1);
                }
            };
            this.checkRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$21 */
    public /* synthetic */ void lambda$checkUserName$21$ChannelCreateActivity(String str) {
        TLRPC$TL_channels_checkUsername tLRPC$TL_channels_checkUsername = new TLRPC$TL_channels_checkUsername();
        tLRPC$TL_channels_checkUsername.username = str;
        tLRPC$TL_channels_checkUsername.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_checkUsername, new RequestDelegate(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChannelCreateActivity.this.lambda$checkUserName$20$ChannelCreateActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$20 */
    public /* synthetic */ void lambda$checkUserName$20$ChannelCreateActivity(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$checkUserName$19$ChannelCreateActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$19 */
    public /* synthetic */ void lambda$checkUserName$19$ChannelCreateActivity(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                if (tLRPC$TL_error == null || !tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChannelCreateActivity$IdBwruYe1uUkO3OmTPecOpVUO7w r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChannelCreateActivity.this.lambda$getThemeDescriptions$22$ChannelCreateActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        $$Lambda$ChannelCreateActivity$IdBwruYe1uUkO3OmTPecOpVUO7w r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$22 */
    public /* synthetic */ void lambda$getThemeDescriptions$22$ChannelCreateActivity() {
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
