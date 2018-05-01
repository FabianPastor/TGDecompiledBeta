package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private EditText editText;
    private HeaderCell headerCell;
    private TextView helpTextView;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditTextBoldCursor nameTextView;
    private String nameToSet;
    private TextBlockCell privateContainer;
    private AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.ChannelCreateActivity$3 */
    class C09683 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChannelCreateActivity$3$1 */
        class C09671 implements DialogInterface.OnClickListener {
            C09671() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChannelCreateActivity.this.avatarUpdater.openCamera();
                } else if (i == 1) {
                    ChannelCreateActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    ChannelCreateActivity.this.avatar = null;
                    ChannelCreateActivity.this.uploadedAvatar = null;
                    ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
                }
            }
        }

        C09683() {
        }

        public void onClick(View view) {
            if (ChannelCreateActivity.this.getParentActivity() != null) {
                view = new Builder(ChannelCreateActivity.this.getParentActivity());
                view.setItems(ChannelCreateActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("DeletePhoto", C0446R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley)}, new C09671());
                ChannelCreateActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$4 */
    class C09694 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09694() {
        }

        public void afterTextChanged(Editable editable) {
            ChannelCreateActivity.this.avatarDrawable.setInfo(5, ChannelCreateActivity.this.nameTextView.length() > 0 ? ChannelCreateActivity.this.nameTextView.getText().toString() : null, null, false);
            ChannelCreateActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$5 */
    class C09705 implements OnEditorActionListener {
        C09705() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChannelCreateActivity.this.doneButton == null) {
                return null;
            }
            ChannelCreateActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$6 */
    class C09716 implements TextWatcher {
        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09716() {
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$7 */
    class C09727 implements OnClickListener {
        C09727() {
        }

        public void onClick(View view) {
            if (ChannelCreateActivity.this.isPrivate != null) {
                ChannelCreateActivity.this.isPrivate = false;
                ChannelCreateActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$8 */
    class C09738 implements OnClickListener {
        C09738() {
        }

        public void onClick(View view) {
            if (ChannelCreateActivity.this.isPrivate == null) {
                ChannelCreateActivity.this.isPrivate = true;
                ChannelCreateActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$9 */
    class C09749 implements TextWatcher {
        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09749() {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$1 */
    class C19741 implements RequestDelegate {
        C19741() {
        }

        public void run(TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    boolean z;
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    if (tL_error != null) {
                        if (tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                            z = false;
                            channelCreateActivity.canCreatePublic = z;
                        }
                    }
                    z = true;
                    channelCreateActivity.canCreatePublic = z;
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.ChannelCreateActivity$2 */
    class C19752 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChannelCreateActivity$2$1 */
        class C09651 implements DialogInterface.OnClickListener {
            C09651() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.createAfterUpload = false;
                ChannelCreateActivity.this.progressDialog = null;
                ChannelCreateActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C19752() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelCreateActivity.this.finishFragment();
            } else if (i == 1) {
                Vibrator vibrator;
                if (ChannelCreateActivity.this.currentStep == 0) {
                    if (ChannelCreateActivity.this.donePressed == 0) {
                        if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                            vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (vibrator != null) {
                                vibrator.vibrate(200);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                            return;
                        }
                        ChannelCreateActivity.this.donePressed = true;
                        if (ChannelCreateActivity.this.avatarUpdater.uploadingAvatar != 0) {
                            ChannelCreateActivity.this.createAfterUpload = true;
                            ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 1);
                            ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                            ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                            ChannelCreateActivity.this.progressDialog.setCancelable(false);
                            ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C09651());
                            ChannelCreateActivity.this.progressDialog.show();
                            return;
                        }
                        i = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, ChannelCreateActivity.this);
                        ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 1);
                        ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                        ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                        ChannelCreateActivity.this.progressDialog.setCancelable(false);
                        ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(i, true);
                                ChannelCreateActivity.this.donePressed = false;
                                try {
                                    dialogInterface.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        });
                        ChannelCreateActivity.this.progressDialog.show();
                    }
                } else if (ChannelCreateActivity.this.currentStep == 1) {
                    if (ChannelCreateActivity.this.isPrivate == 0) {
                        if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                            i = new Builder(ChannelCreateActivity.this.getParentActivity());
                            i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            i.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", C0446R.string.ChannelPublicEmptyUsername));
                            i.setPositiveButton(LocaleController.getString("Close", C0446R.string.Close), null);
                            ChannelCreateActivity.this.showDialog(i.create());
                            return;
                        } else if (ChannelCreateActivity.this.lastNameAvailable == 0) {
                            vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (vibrator != null) {
                                vibrator.vibrate(200);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                            return;
                        } else {
                            MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                        }
                    }
                    i = new Bundle();
                    i.putInt("step", 2);
                    i.putInt("chatId", ChannelCreateActivity.this.chatId);
                    i.putInt("chatType", 2);
                    ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(i), true);
                }
            }
        }
    }

    public ChannelCreateActivity(Bundle bundle) {
        super(bundle);
        this.currentStep = bundle.getInt("step", 0);
        if (this.currentStep == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.avatarUpdater = new AvatarUpdater();
            bundle = new TL_channels_checkUsername();
            bundle.username = "1";
            bundle.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(bundle, new C19741());
            return;
        }
        if (this.currentStep == 1) {
            this.canCreatePublic = bundle.getBoolean("canCreatePublic", true);
            this.isPrivate = true ^ this.canCreatePublic;
            if (!this.canCreatePublic) {
                loadAdminedChannels();
            }
        }
        this.chatId = bundle.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        if (this.avatarUpdater != null) {
            this.avatarUpdater.parentFragment = this;
            this.avatarUpdater.delegate = this;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.avatarUpdater != null) {
            this.avatarUpdater.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C19752());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context2);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context2);
        this.linearLayout.setOrientation(1);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        if (this.currentStep == 0) {
            r0.actionBar.setTitle(LocaleController.getString("NewChannel", C0446R.string.NewChannel));
            r0.fragmentView.setTag(Theme.key_windowBackgroundWhite);
            r0.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            View frameLayout = new FrameLayout(context2);
            r0.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            r0.avatarImage = new BackupImageView(context2);
            r0.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            r0.avatarDrawable.setInfo(5, null, null, false);
            r0.avatarDrawable.setDrawPhoto(true);
            r0.avatarImage.setImageDrawable(r0.avatarDrawable);
            frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            r0.avatarImage.setOnClickListener(new C09683());
            r0.nameTextView = new EditTextBoldCursor(context2);
            r0.nameTextView.setHint(LocaleController.getString("EnterChannelName", C0446R.string.EnterChannelName));
            if (r0.nameToSet != null) {
                r0.nameTextView.setText(r0.nameToSet);
                r0.nameToSet = null;
            }
            r0.nameTextView.setMaxLines(4);
            r0.nameTextView.setGravity(16 | (LocaleController.isRTL ? 5 : 3));
            r0.nameTextView.setTextSize(1, 16.0f);
            r0.nameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.nameTextView.setImeOptions(268435456);
            r0.nameTextView.setInputType(16385);
            r0.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
            r0.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            r0.nameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.nameTextView.setCursorWidth(1.5f);
            frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 16.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 16.0f, 0.0f));
            r0.nameTextView.addTextChangedListener(new C09694());
            r0.descriptionTextView = new EditTextBoldCursor(context2);
            r0.descriptionTextView.setTextSize(1, 18.0f);
            r0.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.descriptionTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            r0.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.descriptionTextView.setInputType(180225);
            r0.descriptionTextView.setImeOptions(6);
            r0.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(120)});
            r0.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", C0446R.string.DescriptionPlaceholder));
            r0.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.descriptionTextView.setCursorWidth(1.5f);
            r0.linearLayout.addView(r0.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            r0.descriptionTextView.setOnEditorActionListener(new C09705());
            r0.descriptionTextView.addTextChangedListener(new C09716());
            r0.helpTextView = new TextView(context2);
            r0.helpTextView.setTextSize(1, 15.0f);
            r0.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            r0.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.helpTextView.setText(LocaleController.getString("DescriptionInfo", C0446R.string.DescriptionInfo));
            r0.linearLayout.addView(r0.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (r0.currentStep == 1) {
            r0.actionBar.setTitle(LocaleController.getString("ChannelSettings", C0446R.string.ChannelSettings));
            r0.fragmentView.setTag(Theme.key_windowBackgroundGray);
            r0.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            r0.linearLayout2 = new LinearLayout(context2);
            r0.linearLayout2.setOrientation(1);
            r0.linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.linearLayout2, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell1 = new RadioButtonCell(context2);
            r0.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", C0446R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", C0446R.string.ChannelPublicInfo), r0.isPrivate ^ true);
            r0.linearLayout2.addView(r0.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell1.setOnClickListener(new C09727());
            r0.radioButtonCell2 = new RadioButtonCell(context2);
            r0.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", C0446R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", C0446R.string.ChannelPrivateInfo), r0.isPrivate);
            r0.linearLayout2.addView(r0.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell2.setOnClickListener(new C09738());
            r0.sectionCell = new ShadowSectionCell(context2);
            r0.linearLayout.addView(r0.sectionCell, LayoutHelper.createLinear(-1, -2));
            r0.linkContainer = new LinearLayout(context2);
            r0.linkContainer.setOrientation(1);
            r0.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.linkContainer, LayoutHelper.createLinear(-1, -2));
            r0.headerCell = new HeaderCell(context2);
            r0.linkContainer.addView(r0.headerCell);
            r0.publicContainer = new LinearLayout(context2);
            r0.publicContainer.setOrientation(0);
            r0.linkContainer.addView(r0.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            r0.editText = new EditText(context2);
            EditText editText = r0.editText;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(r0.currentAccount).linkPrefix);
            stringBuilder.append("/");
            editText.setText(stringBuilder.toString());
            r0.editText.setTextSize(1, 18.0f);
            r0.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.editText.setMaxLines(1);
            r0.editText.setLines(1);
            r0.editText.setEnabled(false);
            r0.editText.setBackgroundDrawable(null);
            r0.editText.setPadding(0, 0, 0, 0);
            r0.editText.setSingleLine(true);
            r0.editText.setInputType(163840);
            r0.editText.setImeOptions(6);
            r0.publicContainer.addView(r0.editText, LayoutHelper.createLinear(-2, 36));
            r0.nameTextView = new EditTextBoldCursor(context2);
            r0.nameTextView.setTextSize(1, 18.0f);
            r0.nameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.nameTextView.setMaxLines(1);
            r0.nameTextView.setLines(1);
            r0.nameTextView.setBackgroundDrawable(null);
            r0.nameTextView.setPadding(0, 0, 0, 0);
            r0.nameTextView.setSingleLine(true);
            r0.nameTextView.setInputType(163872);
            r0.nameTextView.setImeOptions(6);
            r0.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", C0446R.string.ChannelUsernamePlaceholder));
            r0.nameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.nameTextView.setCursorWidth(1.5f);
            r0.publicContainer.addView(r0.nameTextView, LayoutHelper.createLinear(-1, 36));
            r0.nameTextView.addTextChangedListener(new C09749());
            r0.privateContainer = new TextBlockCell(context2);
            r0.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.linkContainer.addView(r0.privateContainer);
            r0.privateContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ChannelCreateActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelCreateActivity.this.invite.link));
                            Toast.makeText(ChannelCreateActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0446R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            r0.checkTextView = new TextView(context2);
            r0.checkTextView.setTextSize(1, 15.0f);
            r0.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.checkTextView.setVisibility(8);
            r0.linkContainer.addView(r0.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            r0.typeInfoCell = new TextInfoPrivacyCell(context2);
            r0.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            r0.linearLayout.addView(r0.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            r0.loadingAdminedCell = new LoadingCell(context2);
            r0.linearLayout.addView(r0.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
            r0.adminnedChannelsLayout = new LinearLayout(context2);
            r0.adminnedChannelsLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.adminnedChannelsLayout.setOrientation(1);
            r0.linearLayout.addView(r0.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            r0.adminedInfoCell = new TextInfoPrivacyCell(context2);
            r0.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            r0.linearLayout.addView(r0.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return r0.fragmentView;
    }

    private void generateLink() {
        if (!this.loadingInvite) {
            if (this.invite == null) {
                this.loadingInvite = true;
                TLObject tL_channels_exportInvite = new TL_channels_exportInvite();
                tL_channels_exportInvite.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_exportInvite, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tL_error == null) {
                                    ChannelCreateActivity.this.invite = (ExportedChatInvite) tLObject;
                                }
                                ChannelCreateActivity.this.loadingInvite = false;
                                ChannelCreateActivity.this.privateContainer.setText(ChannelCreateActivity.this.invite != null ? ChannelCreateActivity.this.invite.link : LocaleController.getString("Loading", C0446R.string.Loading), false);
                            }
                        });
                    }
                });
            }
        }
    }

    private void updatePrivatePublic() {
        if (this.sectionCell != null) {
            int i = 8;
            if (this.isPrivate || this.canCreatePublic) {
                String str;
                int i2;
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(8);
                this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                if (this.isPrivate) {
                    str = "ChannelPrivateLinkHelp";
                    i2 = C0446R.string.ChannelPrivateLinkHelp;
                } else {
                    str = "ChannelUsernameHelp";
                    i2 = C0446R.string.ChannelUsernameHelp;
                }
                textInfoPrivacyCell.setText(LocaleController.getString(str, i2));
                HeaderCell headerCell = this.headerCell;
                if (this.isPrivate) {
                    str = "ChannelInviteLinkTitle";
                    i2 = C0446R.string.ChannelInviteLinkTitle;
                } else {
                    str = "ChannelLinkTitle";
                    i2 = C0446R.string.ChannelLinkTitle;
                }
                headerCell.setText(LocaleController.getString(str, i2));
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", C0446R.string.Loading), false);
                TextView textView = this.checkTextView;
                if (!(this.isPrivate || this.checkTextView.length() == 0)) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", C0446R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(this.isPrivate ^ true, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.nameTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.nameTextView);
        }
    }

    public void didUploadedPhoto(final InputFile inputFile, final PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ChannelCreateActivity.this.uploadedAvatar = inputFile;
                ChannelCreateActivity.this.avatar = photoSize.location;
                ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
                if (ChannelCreateActivity.this.createAfterUpload) {
                    try {
                        if (ChannelCreateActivity.this.progressDialog != null && ChannelCreateActivity.this.progressDialog.isShowing()) {
                            ChannelCreateActivity.this.progressDialog.dismiss();
                            ChannelCreateActivity.this.progressDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    ChannelCreateActivity.this.donePressed = false;
                    ChannelCreateActivity.this.doneButton.performClick();
                }
            }
        });
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
                bundle.putString("path", this.avatarUpdater.currentPicturePath);
            }
            if (this.nameTextView != null) {
                String obj = this.nameTextView.getText().toString();
                if (obj != null && obj.length() != 0) {
                    bundle.putString("nameTextView", obj);
                }
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = bundle.getString("path");
            }
            bundle = bundle.getString("nameTextView");
            if (bundle == null) {
                return;
            }
            if (this.nameTextView != null) {
                this.nameTextView.setText(bundle);
            } else {
                this.nameToSet = bundle;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !this.currentStep) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatDidFailCreate) {
            if (this.progressDialog != 0) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            this.donePressed = false;
        } else if (i == NotificationCenter.chatDidCreated) {
            if (this.progressDialog != 0) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            i = ((Integer) objArr[0]).intValue();
            i2 = new Bundle();
            i2.putInt("step", 1);
            i2.putInt("chat_id", i);
            i2.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(i, this.uploadedAvatar);
            }
            presentFragment(new ChannelCreateActivity(i2), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.ChannelCreateActivity$13$1$1 */
                        class C09621 implements OnClickListener {
                            C09621() {
                            }

                            public void onClick(View view) {
                                view = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                                Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                Object[] objArr;
                                StringBuilder stringBuilder;
                                if (view.megagroup) {
                                    objArr = new Object[2];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).linkPrefix);
                                    stringBuilder.append("/");
                                    stringBuilder.append(view.username);
                                    objArr[0] = stringBuilder.toString();
                                    objArr[1] = view.title;
                                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", C0446R.string.RevokeLinkAlert, objArr)));
                                } else {
                                    objArr = new Object[2];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).linkPrefix);
                                    stringBuilder.append("/");
                                    stringBuilder.append(view.username);
                                    objArr[0] = stringBuilder.toString();
                                    objArr[1] = view.title;
                                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", C0446R.string.RevokeLinkAlertChannel, objArr)));
                                }
                                builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                builder.setPositiveButton(LocaleController.getString("RevokeButton", C0446R.string.RevokeButton), new DialogInterface.OnClickListener() {

                                    /* renamed from: org.telegram.ui.ChannelCreateActivity$13$1$1$1$1 */
                                    class C19721 implements RequestDelegate {

                                        /* renamed from: org.telegram.ui.ChannelCreateActivity$13$1$1$1$1$1 */
                                        class C09601 implements Runnable {
                                            C09601() {
                                            }

                                            public void run() {
                                                ChannelCreateActivity.this.canCreatePublic = true;
                                                if (ChannelCreateActivity.this.nameTextView.length() > 0) {
                                                    ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                                                }
                                                ChannelCreateActivity.this.updatePrivatePublic();
                                            }
                                        }

                                        C19721() {
                                        }

                                        public void run(TLObject tLObject, TL_error tL_error) {
                                            if ((tLObject instanceof TL_boolTrue) != null) {
                                                AndroidUtilities.runOnUIThread(new C09601());
                                            }
                                        }
                                    }

                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface = new TL_channels_updateUsername();
                                        dialogInterface.channel = MessagesController.getInputChannel(view);
                                        dialogInterface.username = TtmlNode.ANONYMOUS_REGION_ID;
                                        ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).sendRequest(dialogInterface, new C19721(), 64);
                                    }
                                });
                                ChannelCreateActivity.this.showDialog(builder.create());
                            }
                        }

                        public void run() {
                            ChannelCreateActivity.this.loadingAdminedChannels = false;
                            if (tLObject != null && ChannelCreateActivity.this.getParentActivity() != null) {
                                for (int i = 0; i < ChannelCreateActivity.this.adminedChannelCells.size(); i++) {
                                    ChannelCreateActivity.this.linearLayout.removeView((View) ChannelCreateActivity.this.adminedChannelCells.get(i));
                                }
                                ChannelCreateActivity.this.adminedChannelCells.clear();
                                TL_messages_chats tL_messages_chats = (TL_messages_chats) tLObject;
                                for (int i2 = 0; i2 < tL_messages_chats.chats.size(); i2++) {
                                    View adminedChannelCell = new AdminedChannelCell(ChannelCreateActivity.this.getParentActivity(), new C09621());
                                    Chat chat = (Chat) tL_messages_chats.chats.get(i2);
                                    boolean z = true;
                                    if (i2 != tL_messages_chats.chats.size() - 1) {
                                        z = false;
                                    }
                                    adminedChannelCell.setChannel(chat, z);
                                    ChannelCreateActivity.this.adminedChannelCells.add(adminedChannelCell);
                                    ChannelCreateActivity.this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
                                }
                                ChannelCreateActivity.this.updatePrivatePublic();
                            }
                        }
                    });
                }
            });
        }
    }

    private boolean checkUserName(final String str) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (!str.startsWith("_")) {
                if (!str.endsWith("_")) {
                    int i = 0;
                    while (i < str.length()) {
                        char charAt = str.charAt(i);
                        if (i == 0 && charAt >= '0' && charAt <= '9') {
                            this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", C0446R.string.LinkInvalidStartNumber));
                            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            return false;
                        } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                            this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0446R.string.LinkInvalid));
                            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            return false;
                        } else {
                            i++;
                        }
                    }
                }
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0446R.string.LinkInvalid));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        }
        if (str != null) {
            if (str.length() >= 5) {
                if (str.length() > 32) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", C0446R.string.LinkInvalidLong));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                }
                this.checkTextView.setText(LocaleController.getString("LinkChecking", C0446R.string.LinkChecking));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                this.lastCheckName = str;
                this.checkRunnable = new Runnable() {

                    /* renamed from: org.telegram.ui.ChannelCreateActivity$14$1 */
                    class C19731 implements RequestDelegate {
                        C19731() {
                        }

                        public void run(final TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ChannelCreateActivity.this.checkReqId = 0;
                                    if (ChannelCreateActivity.this.lastCheckName != null && ChannelCreateActivity.this.lastCheckName.equals(str)) {
                                        if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                                            ChannelCreateActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", C0446R.string.LinkAvailable, str));
                                            ChannelCreateActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                                            ChannelCreateActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                                            ChannelCreateActivity.this.lastNameAvailable = true;
                                            return;
                                        }
                                        if (tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                                            ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", C0446R.string.LinkInUse));
                                        } else {
                                            ChannelCreateActivity.this.canCreatePublic = false;
                                            ChannelCreateActivity.this.loadAdminedChannels();
                                        }
                                        ChannelCreateActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                        ChannelCreateActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                        ChannelCreateActivity.this.lastNameAvailable = false;
                                    }
                                }
                            });
                        }
                    }

                    public void run() {
                        TLObject tL_channels_checkUsername = new TL_channels_checkUsername();
                        tL_channels_checkUsername.username = str;
                        tL_channels_checkUsername.channel = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).getInputChannel(ChannelCreateActivity.this.chatId);
                        ChannelCreateActivity.this.checkReqId = ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).sendRequest(tL_channels_checkUsername, new C19731(), 2);
                    }
                };
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
                return true;
            }
        }
        this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", C0446R.string.LinkInvalidShort));
        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
        return false;
    }

    private void showErrorAlert(String str) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            Object obj = -1;
            int hashCode = str.hashCode();
            if (hashCode != 288843630) {
                if (hashCode == 533175271) {
                    if (str.equals("USERNAME_OCCUPIED") != null) {
                        obj = 1;
                    }
                }
            } else if (str.equals("USERNAME_INVALID") != null) {
                obj = null;
            }
            switch (obj) {
                case null:
                    builder.setMessage(LocaleController.getString("LinkInvalid", C0446R.string.LinkInvalid));
                    break;
                case 1:
                    builder.setMessage(LocaleController.getString("LinkInUse", C0446R.string.LinkInUse));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass15 anonymousClass15 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ChannelCreateActivity.this.adminnedChannelsLayout != null) {
                    int childCount = ChannelCreateActivity.this.adminnedChannelsLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChannelCreateActivity.this.adminnedChannelsLayout.getChildAt(i);
                        if (childAt instanceof AdminedChannelCell) {
                            ((AdminedChannelCell) childAt).update();
                        }
                    }
                }
                if (ChannelCreateActivity.this.avatarImage != null) {
                    ChannelCreateActivity.this.avatarDrawable.setInfo(5, ChannelCreateActivity.this.nameTextView.length() > 0 ? ChannelCreateActivity.this.nameTextView.getText().toString() : null, null, false);
                    ChannelCreateActivity.this.avatarImage.invalidate();
                }
            }
        };
        r10 = new ThemeDescription[54];
        r10[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray);
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r10[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r10[6] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[7] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r10[9] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r10[10] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[11] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[12] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r10[13] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r10[14] = new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        r10[15] = new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[16] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[17] = new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[18] = new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[19] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[20] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[21] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[22] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        r10[23] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText);
        r10[24] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[25] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[26] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[27] = new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[28] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[29] = new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[30] = new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[31] = new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[32] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[33] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[34] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[35] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[36] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[37] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[38] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[39] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[40] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[41] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[42] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[43] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        r10[44] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r10[45] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        AnonymousClass15 anonymousClass152 = anonymousClass15;
        r10[46] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, anonymousClass152, Theme.key_avatar_text);
        r10[47] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundRed);
        r10[48] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundOrange);
        r10[49] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundViolet);
        r10[50] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundGreen);
        r10[51] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundCyan);
        r10[52] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundBlue);
        r10[53] = new ThemeDescription(null, 0, null, null, null, anonymousClass152, Theme.key_avatar_backgroundPink);
        return r10;
    }
}
