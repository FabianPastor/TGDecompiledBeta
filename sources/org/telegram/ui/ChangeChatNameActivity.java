package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeChatNameActivity extends BaseFragment implements AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private View doneButton;
    private boolean donePressed;
    private View headerLabelView;
    private EditTextBoldCursor nameTextView;
    private AlertDialog progressDialog;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$2 */
    class C09062 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C09062() {
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$3 */
    class C09083 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$3$1 */
        class C09071 implements DialogInterface.OnClickListener {
            C09071() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChangeChatNameActivity.this.avatarUpdater.openCamera();
                } else if (i == 1) {
                    ChangeChatNameActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    ChangeChatNameActivity.this.avatar = null;
                    ChangeChatNameActivity.this.uploadedAvatar = null;
                    ChangeChatNameActivity.this.avatarImage.setImage(ChangeChatNameActivity.this.avatar, "50_50", ChangeChatNameActivity.this.avatarDrawable);
                }
            }
        }

        C09083() {
        }

        public void onClick(View view) {
            if (ChangeChatNameActivity.this.getParentActivity() != null) {
                view = new Builder(ChangeChatNameActivity.this.getParentActivity());
                view.setItems(ChangeChatNameActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("DeletePhoto", C0446R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley)}, new C09071());
                ChangeChatNameActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$4 */
    class C09094 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09094() {
        }

        public void afterTextChanged(Editable editable) {
            ChangeChatNameActivity.this.avatarDrawable.setInfo(5, ChangeChatNameActivity.this.nameTextView.length() > 0 ? ChangeChatNameActivity.this.nameTextView.getText().toString() : null, null, false);
            ChangeChatNameActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$5 */
    class C09115 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$5$1 */
        class C09101 implements DialogInterface.OnClickListener {
            C09101() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                if (AndroidUtilities.isTablet() != null) {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) ChangeChatNameActivity.this.chatId)));
                } else {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).deleteUserFromChat(ChangeChatNameActivity.this.chatId, MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChangeChatNameActivity.this.currentAccount).getClientUserId())), null, true);
                ChangeChatNameActivity.this.finishFragment();
            }
        }

        C09115() {
        }

        public void onClick(View view) {
            view = new Builder(ChangeChatNameActivity.this.getParentActivity());
            view.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0446R.string.AreYouSureDeleteAndExit));
            view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C09101());
            view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            ChangeChatNameActivity.this.showDialog(view.create());
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$7 */
    class C09137 implements Runnable {
        C09137() {
        }

        public void run() {
            if (ChangeChatNameActivity.this.nameTextView != null) {
                ChangeChatNameActivity.this.nameTextView.requestFocus();
                AndroidUtilities.showKeyboard(ChangeChatNameActivity.this.nameTextView);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$1 */
    class C19491 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$1$1 */
        class C09051 implements DialogInterface.OnClickListener {
            C09051() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChangeChatNameActivity.this.createAfterUpload = false;
                ChangeChatNameActivity.this.progressDialog = null;
                ChangeChatNameActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C19491() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeChatNameActivity.this.finishFragment();
            } else if (i != 1 || ChangeChatNameActivity.this.donePressed != 0) {
            } else {
                if (ChangeChatNameActivity.this.nameTextView.length() == 0) {
                    Vibrator vibrator = (Vibrator) ChangeChatNameActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChangeChatNameActivity.this.nameTextView, 2.0f, 0);
                    return;
                }
                ChangeChatNameActivity.this.donePressed = true;
                if (ChangeChatNameActivity.this.avatarUpdater.uploadingAvatar != 0) {
                    ChangeChatNameActivity.this.createAfterUpload = true;
                    ChangeChatNameActivity.this.progressDialog = new AlertDialog(ChangeChatNameActivity.this.getParentActivity(), 1);
                    ChangeChatNameActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                    ChangeChatNameActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    ChangeChatNameActivity.this.progressDialog.setCancelable(false);
                    ChangeChatNameActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C09051());
                    ChangeChatNameActivity.this.progressDialog.show();
                    return;
                }
                if (ChangeChatNameActivity.this.uploadedAvatar != 0) {
                    MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, ChangeChatNameActivity.this.uploadedAvatar);
                } else if (ChangeChatNameActivity.this.avatar == 0 && (ChangeChatNameActivity.this.currentChat.photo instanceof TL_chatPhoto) != 0) {
                    MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, null);
                }
                ChangeChatNameActivity.this.finishFragment();
                if (ChangeChatNameActivity.this.nameTextView.getText().length() != 0) {
                    ChangeChatNameActivity.this.saveName();
                    ChangeChatNameActivity.this.finishFragment();
                }
            }
        }
    }

    public ChangeChatNameActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarDrawable = new AvatarDrawable();
        this.chatId = getArguments().getInt("chat_id", 0);
        this.avatarUpdater = new AvatarUpdater();
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        return true;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", C0446R.string.ChannelEdit));
        this.actionBar.setActionBarMenuOnItemClick(new C19491());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        View linearLayout = new LinearLayout(context2);
        this.fragmentView = linearLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new C09062());
        View linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        View frameLayout = new FrameLayout(context2);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        int i = 5;
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
        float f = 16.0f;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        r0.avatarImage.setOnClickListener(new C09083());
        r0.nameTextView = new EditTextBoldCursor(context2);
        if (r0.currentChat.megagroup) {
            r0.nameTextView.setHint(LocaleController.getString("GroupName", C0446R.string.GroupName));
        } else {
            r0.nameTextView.setHint(LocaleController.getString("EnterChannelName", C0446R.string.EnterChannelName));
        }
        r0.nameTextView.setMaxLines(4);
        r0.nameTextView.setText(r0.currentChat.title);
        EditTextBoldCursor editTextBoldCursor = r0.nameTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        editTextBoldCursor.setGravity(i | 16);
        r0.nameTextView.setTextSize(1, f);
        r0.nameTextView.setHint(LocaleController.getString("GroupName", C0446R.string.GroupName));
        r0.nameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.nameTextView.setImeOptions(268435456);
        r0.nameTextView.setInputType(16385);
        r0.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.nameTextView.setFocusable(r0.nameTextView.isEnabled());
        r0.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
        r0.nameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.nameTextView.setCursorWidth(1.5f);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : f, 0.0f));
        r0.nameTextView.addTextChangedListener(new C09094());
        View shadowSectionCell = new ShadowSectionCell(context2);
        shadowSectionCell.setSize(20);
        linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (r0.currentChat.creator) {
            shadowSectionCell = new FrameLayout(context2);
            shadowSectionCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
            linearLayout2 = new TextSettingsCell(context2);
            linearLayout2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            linearLayout2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            linearLayout2.setText(LocaleController.getString("DeleteMega", C0446R.string.DeleteMega), false);
            shadowSectionCell.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f));
            linearLayout2.setOnClickListener(new C09115());
            shadowSectionCell = new TextInfoPrivacyCell(context2);
            shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            shadowSectionCell.setText(LocaleController.getString("MegaDeleteInfo", C0446R.string.MegaDeleteInfo));
            linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
        r0.nameTextView.setSelection(r0.nameTextView.length());
        if (r0.currentChat.photo != null) {
            r0.avatar = r0.currentChat.photo.photo_small;
            r0.avatarImage.setImage(r0.avatar, "50_50", r0.avatarDrawable);
        } else {
            r0.avatarImage.setImageDrawable(r0.avatarDrawable);
        }
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void didUploadedPhoto(final InputFile inputFile, final PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ChangeChatNameActivity.this.uploadedAvatar = inputFile;
                ChangeChatNameActivity.this.avatar = photoSize.location;
                ChangeChatNameActivity.this.avatarImage.setImage(ChangeChatNameActivity.this.avatar, "50_50", ChangeChatNameActivity.this.avatarDrawable);
                if (ChangeChatNameActivity.this.createAfterUpload) {
                    ChangeChatNameActivity.this.donePressed = false;
                    try {
                        if (ChangeChatNameActivity.this.progressDialog != null && ChangeChatNameActivity.this.progressDialog.isShowing()) {
                            ChangeChatNameActivity.this.progressDialog.dismiss();
                            ChangeChatNameActivity.this.progressDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    ChangeChatNameActivity.this.doneButton.performClick();
                }
            }
        });
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
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

    public void restoreSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C09137(), 100);
        }
    }

    private void saveName() {
        MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.nameTextView.getText().toString());
    }
}
