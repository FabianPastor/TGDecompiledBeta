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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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
    class C09002 implements OnTouchListener {
        C09002() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$3 */
    class C09023 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$3$1 */
        class C09011 implements DialogInterface.OnClickListener {
            C09011() {
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

        C09023() {
        }

        public void onClick(View view) {
            if (ChangeChatNameActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChangeChatNameActivity.this.getParentActivity());
                builder.setItems(ChangeChatNameActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)}, new C09011());
                ChangeChatNameActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$4 */
    class C09034 implements TextWatcher {
        C09034() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ChangeChatNameActivity.this.avatarDrawable.setInfo(5, ChangeChatNameActivity.this.nameTextView.length() > 0 ? ChangeChatNameActivity.this.nameTextView.getText().toString() : null, null, false);
            ChangeChatNameActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$5 */
    class C09055 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$5$1 */
        class C09041 implements DialogInterface.OnClickListener {
            C09041() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) ChangeChatNameActivity.this.chatId)));
                } else {
                    NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).deleteUserFromChat(ChangeChatNameActivity.this.chatId, MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChangeChatNameActivity.this.currentAccount).getClientUserId())), null, true);
                ChangeChatNameActivity.this.finishFragment();
            }
        }

        C09055() {
        }

        public void onClick(View v) {
            Builder builder = new Builder(ChangeChatNameActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C09041());
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            ChangeChatNameActivity.this.showDialog(builder.create());
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$7 */
    class C09077 implements Runnable {
        C09077() {
        }

        public void run() {
            if (ChangeChatNameActivity.this.nameTextView != null) {
                ChangeChatNameActivity.this.nameTextView.requestFocus();
                AndroidUtilities.showKeyboard(ChangeChatNameActivity.this.nameTextView);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeChatNameActivity$1 */
    class C19431 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChangeChatNameActivity$1$1 */
        class C08991 implements DialogInterface.OnClickListener {
            C08991() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ChangeChatNameActivity.this.createAfterUpload = false;
                ChangeChatNameActivity.this.progressDialog = null;
                ChangeChatNameActivity.this.donePressed = false;
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C19431() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeChatNameActivity.this.finishFragment();
            } else if (id == 1 && !ChangeChatNameActivity.this.donePressed) {
                if (ChangeChatNameActivity.this.nameTextView.length() == 0) {
                    Vibrator v = (Vibrator) ChangeChatNameActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChangeChatNameActivity.this.nameTextView, 2.0f, 0);
                    return;
                }
                ChangeChatNameActivity.this.donePressed = true;
                if (ChangeChatNameActivity.this.avatarUpdater.uploadingAvatar != null) {
                    ChangeChatNameActivity.this.createAfterUpload = true;
                    ChangeChatNameActivity.this.progressDialog = new AlertDialog(ChangeChatNameActivity.this.getParentActivity(), 1);
                    ChangeChatNameActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    ChangeChatNameActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    ChangeChatNameActivity.this.progressDialog.setCancelable(false);
                    ChangeChatNameActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C08991());
                    ChangeChatNameActivity.this.progressDialog.show();
                    return;
                }
                if (ChangeChatNameActivity.this.uploadedAvatar != null) {
                    MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, ChangeChatNameActivity.this.uploadedAvatar);
                } else if (ChangeChatNameActivity.this.avatar == null && (ChangeChatNameActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
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

    public ChangeChatNameActivity(Bundle args) {
        super(args);
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
        this.actionBar.setActionBarMenuOnItemClick(new C19431());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.fragmentView = linearLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new C09002());
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
        int i = 3;
        float f = 16.0f;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        r0.avatarImage.setOnClickListener(new C09023());
        r0.nameTextView = new EditTextBoldCursor(context2);
        if (r0.currentChat.megagroup) {
            r0.nameTextView.setHint(LocaleController.getString("GroupName", R.string.GroupName));
        } else {
            r0.nameTextView.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
        }
        r0.nameTextView.setMaxLines(4);
        r0.nameTextView.setText(r0.currentChat.title);
        EditTextBoldCursor editTextBoldCursor = r0.nameTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor.setGravity(16 | i);
        r0.nameTextView.setTextSize(1, f);
        r0.nameTextView.setHint(LocaleController.getString("GroupName", R.string.GroupName));
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
        r0.nameTextView.addTextChangedListener(new C09034());
        ShadowSectionCell sectionCell = new ShadowSectionCell(context2);
        sectionCell.setSize(20);
        linearLayout.addView(sectionCell, LayoutHelper.createLinear(-1, -2));
        if (r0.currentChat.creator) {
            FrameLayout container3 = new FrameLayout(context2);
            container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout.addView(container3, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textCell = new TextSettingsCell(context2);
            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textCell.setText(LocaleController.getString("DeleteMega", R.string.DeleteMega), false);
            container3.addView(textCell, LayoutHelper.createFrame(-1, -2.0f));
            textCell.setOnClickListener(new C09055());
            TextInfoPrivacyCell infoCell2 = new TextInfoPrivacyCell(context2);
            infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            infoCell2.setText(LocaleController.getString("MegaDeleteInfo", R.string.MegaDeleteInfo));
            linearLayout.addView(infoCell2, LayoutHelper.createLinear(-1, -2));
        } else {
            sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
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

    public void didUploadedPhoto(final InputFile file, final PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ChangeChatNameActivity.this.uploadedAvatar = file;
                ChangeChatNameActivity.this.avatar = small.location;
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.nameTextView != null) {
            String text = this.nameTextView.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new C09077(), 100);
        }
    }

    private void saveName() {
        MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.nameTextView.getText().toString());
    }
}
