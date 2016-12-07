package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditTypeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private TextInfoPrivacyCell adminedInfoCell;
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private Chat currentChat;
    private boolean donePressed;
    private HeaderCell headerCell;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditText nameTextView;
    private TextBlockCell privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;

    public ChannelEditTypeActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        boolean z = false;
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            final Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    ChannelEditTypeActivity.this.currentChat = MessagesStorage.getInstance().getChat(ChannelEditTypeActivity.this.chatId);
                    semaphore.release();
                }
            });
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance().putChat(this.currentChat, true);
        }
        if (this.currentChat.username == null || this.currentChat.username.length() == 0) {
            z = true;
        }
        this.isPrivate = z;
        if (this.isPrivate) {
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChannelEditTypeActivity channelEditTypeActivity = ChannelEditTypeActivity.this;
                            boolean z = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
                            channelEditTypeActivity.canCreatePublic = z;
                            if (!ChannelEditTypeActivity.this.canCreatePublic) {
                                ChannelEditTypeActivity.this.loadAdminedChannels();
                            }
                        }
                    });
                }
            });
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelEditTypeActivity.this.finishFragment();
                } else if (id == 1 && !ChannelEditTypeActivity.this.donePressed) {
                    if (ChannelEditTypeActivity.this.isPrivate || (((ChannelEditTypeActivity.this.currentChat.username != null || ChannelEditTypeActivity.this.nameTextView.length() == 0) && (ChannelEditTypeActivity.this.currentChat.username == null || ChannelEditTypeActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditTypeActivity.this.nameTextView.getText().toString()))) || ChannelEditTypeActivity.this.nameTextView.length() == 0 || ChannelEditTypeActivity.this.lastNameAvailable)) {
                        ChannelEditTypeActivity.this.donePressed = true;
                        String oldUserName = ChannelEditTypeActivity.this.currentChat.username != null ? ChannelEditTypeActivity.this.currentChat.username : "";
                        String newUserName = ChannelEditTypeActivity.this.isPrivate ? "" : ChannelEditTypeActivity.this.nameTextView.getText().toString();
                        if (!oldUserName.equals(newUserName)) {
                            MessagesController.getInstance().updateChannelUserName(ChannelEditTypeActivity.this.chatId, newUserName);
                        }
                        ChannelEditTypeActivity.this.finishFragment();
                        return;
                    }
                    Vibrator v = (Vibrator) ChannelEditTypeActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditTypeActivity.this.checkTextView, 2.0f, 0);
                }
            }
        });
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupType", R.string.GroupType));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ChannelType", R.string.ChannelType));
        }
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(-1);
        this.linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(context);
        this.radioButtonCell1.setBackgroundResource(R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", R.string.MegaPublicInfo), !this.isPrivate, false);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), !this.isPrivate, false);
        }
        linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelEditTypeActivity.this.isPrivate) {
                    ChannelEditTypeActivity.this.isPrivate = false;
                    ChannelEditTypeActivity.this.updatePrivatePublic();
                }
            }
        });
        this.radioButtonCell2 = new RadioButtonCell(context);
        this.radioButtonCell2.setBackgroundResource(R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", R.string.MegaPrivateInfo), this.isPrivate, false);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), this.isPrivate, false);
        }
        linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!ChannelEditTypeActivity.this.isPrivate) {
                    ChannelEditTypeActivity.this.isPrivate = true;
                    ChannelEditTypeActivity.this.updatePrivatePublic();
                }
            }
        });
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.linkContainer = new LinearLayout(context);
        this.linkContainer.setOrientation(1);
        this.linkContainer.setBackgroundColor(-1);
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.linkContainer.addView(this.headerCell);
        this.publicContainer = new LinearLayout(context);
        this.publicContainer.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
        EditText editText = new EditText(context);
        editText.setText("telegram.me/");
        editText.setTextSize(1, 18.0f);
        editText.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        editText.setTextColor(-14606047);
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setEnabled(false);
        editText.setBackgroundDrawable(null);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setInputType(163840);
        editText.setImeOptions(6);
        this.publicContainer.addView(editText, LayoutHelper.createLinear(-2, 36));
        this.nameTextView = new EditText(context);
        this.nameTextView.setTextSize(1, 18.0f);
        if (!this.isPrivate) {
            this.nameTextView.setText(this.currentChat.username);
        }
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setTextColor(-14606047);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setLines(1);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setPadding(0, 0, 0, 0);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setInputType(163872);
        this.nameTextView.setImeOptions(6);
        this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", R.string.ChannelUsernamePlaceholder));
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
        this.nameTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        this.privateContainer = new TextBlockCell(context);
        this.privateContainer.setBackgroundResource(R.drawable.list_selector);
        this.linkContainer.addView(this.privateContainer);
        this.privateContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelEditTypeActivity.this.invite != null) {
                    try {
                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditTypeActivity.this.invite.link));
                        Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }
        });
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.checkTextView.setVisibility(8);
        this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
        this.typeInfoCell = new TextInfoPrivacyCell(context);
        this.typeInfoCell.setBackgroundResource(R.drawable.greydivider_bottom);
        this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        this.loadingAdminedCell = new LoadingCell(context);
        this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
        this.adminedInfoCell = new TextInfoPrivacyCell(context);
        this.adminedInfoCell.setBackgroundResource(R.drawable.greydivider_bottom);
        this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
        updatePrivatePublic();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite instanceof TL_chatInviteExported) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink();
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance().sendRequest(new TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChannelEditTypeActivity.this.loadingAdminedChannels = false;
                            if (response != null && ChannelEditTypeActivity.this.getParentActivity() != null) {
                                int a;
                                for (a = 0; a < ChannelEditTypeActivity.this.adminedChannelCells.size(); a++) {
                                    ChannelEditTypeActivity.this.linearLayout.removeView((View) ChannelEditTypeActivity.this.adminedChannelCells.get(a));
                                }
                                ChannelEditTypeActivity.this.adminedChannelCells.clear();
                                TL_messages_chats res = response;
                                a = 0;
                                while (a < res.chats.size()) {
                                    AdminedChannelCell adminedChannelCell = new AdminedChannelCell(ChannelEditTypeActivity.this.getParentActivity(), new OnClickListener() {
                                        public void onClick(View view) {
                                            final Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                                            Builder builder = new Builder(ChannelEditTypeActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            if (channel.megagroup) {
                                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, "telegram.me/" + channel.username, channel.title)));
                                            } else {
                                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, "telegram.me/" + channel.username, channel.title)));
                                            }
                                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                            builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    TL_channels_updateUsername req = new TL_channels_updateUsername();
                                                    req.channel = MessagesController.getInputChannel(channel);
                                                    req.username = "";
                                                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                                        public void run(TLObject response, TL_error error) {
                                                            if (response instanceof TL_boolTrue) {
                                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                                    public void run() {
                                                                        ChannelEditTypeActivity.this.canCreatePublic = true;
                                                                        if (ChannelEditTypeActivity.this.nameTextView.length() > 0) {
                                                                            ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
                                                                        }
                                                                        ChannelEditTypeActivity.this.updatePrivatePublic();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }, 64);
                                                }
                                            });
                                            ChannelEditTypeActivity.this.showDialog(builder.create());
                                        }
                                    });
                                    adminedChannelCell.setChannel((Chat) res.chats.get(a), a == res.chats.size() + -1);
                                    ChannelEditTypeActivity.this.adminedChannelCells.add(adminedChannelCell);
                                    ChannelEditTypeActivity.this.linearLayout.addView(adminedChannelCell, ChannelEditTypeActivity.this.linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, 72));
                                    a++;
                                }
                                ChannelEditTypeActivity.this.updatePrivatePublic();
                            }
                        }
                    });
                }
            });
        }
    }

    private void updatePrivatePublic() {
        int i = 8;
        boolean z = false;
        if (this.sectionCell != null) {
            int a;
            if (this.isPrivate || this.canCreatePublic) {
                int i2;
                this.typeInfoCell.setTextColor(-8355712);
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.typeInfoCell.setBackgroundResource(R.drawable.greydivider_bottom);
                for (a = 0; a < this.adminedChannelCells.size(); a++) {
                    ((AdminedChannelCell) this.adminedChannelCells.get(a)).setVisibility(8);
                }
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.currentChat.megagroup) {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("MegaPrivateLinkHelp", R.string.MegaPrivateLinkHelp) : LocaleController.getString("MegaUsernameHelp", R.string.MegaUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
                } else {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", R.string.ChannelUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
                }
                LinearLayout linearLayout = this.publicContainer;
                if (this.isPrivate) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                linearLayout.setVisibility(i2);
                TextBlockCell textBlockCell = this.privateContainer;
                if (this.isPrivate) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                textBlockCell.setVisibility(i2);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
                TextView textView = this.checkTextView;
                if (!(this.isPrivate || this.checkTextView.length() == 0)) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTextColor(-3198928);
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    for (a = 0; a < this.adminedChannelCells.size(); a++) {
                        ((AdminedChannelCell) this.adminedChannelCells.get(a)).setVisibility(8);
                    }
                    this.typeInfoCell.setBackgroundResource(R.drawable.greydivider_bottom);
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    this.typeInfoCell.setBackgroundResource(R.drawable.greydivider);
                    this.loadingAdminedCell.setVisibility(8);
                    for (a = 0; a < this.adminedChannelCells.size(); a++) {
                        ((AdminedChannelCell) this.adminedChannelCells.get(a)).setVisibility(0);
                    }
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            RadioButtonCell radioButtonCell = this.radioButtonCell1;
            if (!this.isPrivate) {
                z = true;
            }
            radioButtonCell.setChecked(z, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.nameTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.nameTextView);
        }
    }

    private boolean checkUserName(final String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a != 0 || ch < '0' || ch > '9') {
                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                        this.checkTextView.setTextColor(-3198928);
                        return false;
                    }
                    a++;
                } else if (this.currentChat.megagroup) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", R.string.LinkInvalidStartNumberMega));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.currentChat.megagroup) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", R.string.LinkInvalidShortMega));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            this.checkTextView.setTextColor(-3198928);
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTextColor(-3198928);
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTextColor(-9605774);
            this.lastCheckName = name;
            this.checkRunnable = new Runnable() {
                public void run() {
                    TL_channels_checkUsername req = new TL_channels_checkUsername();
                    req.username = name;
                    req.channel = MessagesController.getInputChannel(ChannelEditTypeActivity.this.chatId);
                    ChannelEditTypeActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ChannelEditTypeActivity.this.checkReqId = 0;
                                    if (ChannelEditTypeActivity.this.lastCheckName != null && ChannelEditTypeActivity.this.lastCheckName.equals(name)) {
                                        if (error == null && (response instanceof TL_boolTrue)) {
                                            ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                                            ChannelEditTypeActivity.this.checkTextView.setTextColor(-14248148);
                                            ChannelEditTypeActivity.this.lastNameAvailable = true;
                                            return;
                                        }
                                        if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                                            ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                                        } else {
                                            ChannelEditTypeActivity.this.canCreatePublic = false;
                                            ChannelEditTypeActivity.this.loadAdminedChannels();
                                        }
                                        ChannelEditTypeActivity.this.checkTextView.setTextColor(-3198928);
                                        ChannelEditTypeActivity.this.lastNameAvailable = false;
                                    }
                                }
                            });
                        }
                    }, 2);
                }
            };
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TL_channels_exportInvite req = new TL_channels_exportInvite();
            req.channel = MessagesController.getInputChannel(this.chatId);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                ChannelEditTypeActivity.this.invite = (ExportedChatInvite) response;
                            }
                            ChannelEditTypeActivity.this.loadingInvite = false;
                            ChannelEditTypeActivity.this.privateContainer.setText(ChannelEditTypeActivity.this.invite != null ? ChannelEditTypeActivity.this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
                        }
                    });
                }
            });
        }
    }
}
