package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChipSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterSectionsListView;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private TextInfoPrivacyCell adminedInfoCell;
    private ArrayList<ChipSpan> allSpans = new ArrayList();
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int beforeChangeIndex;
    private boolean canCreatePublic = true;
    private CharSequence changeString;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditText descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextView emptyTextView;
    private HeaderCell headerCell;
    private boolean ignoreChange;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linkContainer;
    private LetterSectionsListView listView;
    private ContactsAdapter listViewAdapter;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditText nameTextView;
    private String nameToSet;
    private TextBlockCell privateContainer;
    private ProgressDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private ShadowSectionCell sectionCell;
    private HashMap<Integer, ChipSpan> selectedContacts = new HashMap();
    private TextInfoPrivacyCell typeInfoCell;
    private InputFile uploadedAvatar;

    public ChannelCreateActivity(Bundle args) {
        boolean z = true;
        super(args);
        this.currentStep = args.getInt("step", 0);
        if (this.currentStep == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.avatarUpdater = new AvatarUpdater();
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                            boolean z = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
                            channelCreateActivity.canCreatePublic = z;
                        }
                    });
                }
            });
            return;
        }
        if (this.currentStep == 1) {
            this.canCreatePublic = args.getBoolean("canCreatePublic", true);
            if (this.canCreatePublic) {
                z = false;
            }
            this.isPrivate = z;
            if (!this.canCreatePublic) {
                loadAdminedChannels();
            }
        }
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 2) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        } else if (this.currentStep == 1) {
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
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 2) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        }
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
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelCreateActivity.this.finishFragment();
                } else if (id != 1) {
                } else {
                    Vibrator v;
                    if (ChannelCreateActivity.this.currentStep == 0) {
                        if (!ChannelCreateActivity.this.donePressed) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                v = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                                return;
                            }
                            ChannelCreateActivity.this.donePressed = true;
                            if (ChannelCreateActivity.this.avatarUpdater.uploadingAvatar != null) {
                                ChannelCreateActivity.this.createAfterUpload = true;
                                ChannelCreateActivity.this.progressDialog = new ProgressDialog(ChannelCreateActivity.this.getParentActivity());
                                ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                                ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                                ChannelCreateActivity.this.progressDialog.setCancelable(false);
                                ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChannelCreateActivity.this.createAfterUpload = false;
                                        ChannelCreateActivity.this.progressDialog = null;
                                        ChannelCreateActivity.this.donePressed = false;
                                        try {
                                            dialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.e("tmessages", e);
                                        }
                                    }
                                });
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            final int reqId = MessagesController.getInstance().createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, ChannelCreateActivity.this);
                            ChannelCreateActivity.this.progressDialog = new ProgressDialog(ChannelCreateActivity.this.getParentActivity());
                            ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                            ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                            ChannelCreateActivity.this.progressDialog.setCancelable(false);
                            ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ConnectionsManager.getInstance().cancelRequest(reqId, true);
                                    ChannelCreateActivity.this.donePressed = false;
                                    try {
                                        dialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.e("tmessages", e);
                                    }
                                }
                            });
                            ChannelCreateActivity.this.progressDialog.show();
                        }
                    } else if (ChannelCreateActivity.this.currentStep == 1) {
                        if (!ChannelCreateActivity.this.isPrivate) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", R.string.ChannelPublicEmptyUsername));
                                builder.setPositiveButton(LocaleController.getString("Close", R.string.Close), null);
                                ChannelCreateActivity.this.showDialog(builder.create());
                                return;
                            } else if (ChannelCreateActivity.this.lastNameAvailable) {
                                MessagesController.getInstance().updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                            } else {
                                v = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                return;
                            }
                        }
                        Bundle args = new Bundle();
                        args.putInt("step", 2);
                        args.putInt("chat_id", ChannelCreateActivity.this.chatId);
                        ChannelCreateActivity.this.presentFragment(new ChannelCreateActivity(args), true);
                    } else {
                        ArrayList<InputUser> result = new ArrayList();
                        for (Integer uid : ChannelCreateActivity.this.selectedContacts.keySet()) {
                            InputUser user = MessagesController.getInputUser(MessagesController.getInstance().getUser(uid));
                            if (user != null) {
                                result.add(user);
                            }
                        }
                        MessagesController.getInstance().addUsersToChannel(ChannelCreateActivity.this.chatId, result, null);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        Bundle args2 = new Bundle();
                        args2.putInt("chat_id", ChannelCreateActivity.this.chatId);
                        ChannelCreateActivity.this.presentFragment(new ChatActivity(args2), true);
                    }
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        if (this.currentStep != 2) {
            this.fragmentView = new ScrollView(context);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            this.linearLayout = new LinearLayout(context);
            scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        } else {
            this.fragmentView = new LinearLayout(context);
            this.fragmentView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            this.linearLayout = (LinearLayout) this.fragmentView;
        }
        this.linearLayout.setOrientation(1);
        FrameLayout frameLayout;
        if (this.currentStep == 0) {
            float f;
            float f2;
            this.actionBar.setTitle(LocaleController.getString("NewChannel", R.string.NewChannel));
            this.fragmentView.setBackgroundColor(-1);
            frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.avatarImage = new BackupImageView(context);
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, null, null, false);
            this.avatarDrawable.setDrawPhoto(true);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            View view = this.avatarImage;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = 16.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 16.0f;
            } else {
                f2 = 0.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
            this.avatarImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (ChannelCreateActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                        builder.setItems(ChannelCreateActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)}, new OnClickListener() {
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
                        });
                        ChannelCreateActivity.this.showDialog(builder.create());
                    }
                }
            });
            this.nameTextView = new EditText(context);
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
            if (this.nameToSet != null) {
                this.nameTextView.setText(this.nameToSet);
                this.nameToSet = null;
            }
            this.nameTextView.setMaxLines(4);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.nameTextView.setTextSize(1, 16.0f);
            this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.nameTextView.setImeOptions(268435456);
            this.nameTextView.setInputType(16385);
            this.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
            this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            AndroidUtilities.clearCursorDrawable(this.nameTextView);
            this.nameTextView.setTextColor(-14606047);
            view = this.nameTextView;
            f = LocaleController.isRTL ? 16.0f : 96.0f;
            if (LocaleController.isRTL) {
                f2 = 96.0f;
            } else {
                f2 = 16.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f, 0.0f, f2, 0.0f));
            this.nameTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    String obj;
                    AvatarDrawable access$1600 = ChannelCreateActivity.this.avatarDrawable;
                    if (ChannelCreateActivity.this.nameTextView.length() > 0) {
                        obj = ChannelCreateActivity.this.nameTextView.getText().toString();
                    } else {
                        obj = null;
                    }
                    access$1600.setInfo(5, obj, null, false);
                    ChannelCreateActivity.this.avatarImage.invalidate();
                }
            });
            this.descriptionTextView = new EditText(context);
            this.descriptionTextView.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.descriptionTextView.setTextColor(-14606047);
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", R.string.DescriptionPlaceholder));
            AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 6 || ChannelCreateActivity.this.doneButton == null) {
                        return false;
                    }
                    ChannelCreateActivity.this.doneButton.performClick();
                    return true;
                }
            });
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            TextView helpTextView = new TextView(context);
            helpTextView.setTextSize(1, 15.0f);
            helpTextView.setTextColor(-9605774);
            helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            helpTextView.setText(LocaleController.getString("DescriptionInfo", R.string.DescriptionInfo));
            LinearLayout linearLayout = this.linearLayout;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            linearLayout.addView(helpTextView, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 20));
        } else if (this.currentStep == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelSettings", R.string.ChannelSettings));
            this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(1);
            linearLayout2.setBackgroundColor(-1);
            this.linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1 = new RadioButtonCell(context);
            this.radioButtonCell1.setBackgroundResource(R.drawable.list_selector);
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), !this.isPrivate, false);
            linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ChannelCreateActivity.this.isPrivate) {
                        ChannelCreateActivity.this.isPrivate = false;
                        ChannelCreateActivity.this.updatePrivatePublic();
                    }
                }
            });
            this.radioButtonCell2 = new RadioButtonCell(context);
            this.radioButtonCell2.setBackgroundResource(R.drawable.list_selector);
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), this.isPrivate, false);
            linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!ChannelCreateActivity.this.isPrivate) {
                        ChannelCreateActivity.this.isPrivate = true;
                        ChannelCreateActivity.this.updatePrivatePublic();
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
                    ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            this.privateContainer = new TextBlockCell(context);
            this.privateContainer.setBackgroundResource(R.drawable.list_selector);
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ChannelCreateActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelCreateActivity.this.invite.link));
                            Toast.makeText(ChannelCreateActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
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
        } else if (this.currentStep == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", R.string.ChannelAddMembers));
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            this.searchListViewAdapter = new SearchAdapter(context, null, false, false, false, false);
            this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
            this.searchListViewAdapter.setUseUserCell(true);
            this.listViewAdapter = new ContactsAdapter(context, 1, false, null, false);
            this.listViewAdapter.setCheckedMap(this.selectedContacts);
            frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.nameTextView = new EditText(context);
            this.nameTextView.setTextSize(1, 16.0f);
            this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.nameTextView.setTextColor(-14606047);
            this.nameTextView.setInputType(655536);
            this.nameTextView.setMinimumHeight(AndroidUtilities.dp(54.0f));
            this.nameTextView.setSingleLine(false);
            this.nameTextView.setLines(2);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setVerticalScrollBarEnabled(true);
            this.nameTextView.setHorizontalScrollBarEnabled(false);
            this.nameTextView.setPadding(0, 0, 0, 0);
            this.nameTextView.setHint(LocaleController.getString("AddMutual", R.string.AddMutual));
            this.nameTextView.setTextIsSelectable(false);
            this.nameTextView.setImeOptions(268435462);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            AndroidUtilities.clearCursorDrawable(this.nameTextView);
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.nameTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    if (!ChannelCreateActivity.this.ignoreChange) {
                        ChannelCreateActivity.this.beforeChangeIndex = ChannelCreateActivity.this.nameTextView.getSelectionStart();
                        ChannelCreateActivity.this.changeString = new SpannableString(charSequence);
                    }
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!ChannelCreateActivity.this.ignoreChange) {
                        boolean search = false;
                        int afterChangeIndex = ChannelCreateActivity.this.nameTextView.getSelectionEnd();
                        if (editable.toString().length() < ChannelCreateActivity.this.changeString.toString().length()) {
                            String deletedString = "";
                            try {
                                deletedString = ChannelCreateActivity.this.changeString.toString().substring(afterChangeIndex, ChannelCreateActivity.this.beforeChangeIndex);
                            } catch (Throwable e) {
                                FileLog.e("tmessages", e);
                            }
                            if (deletedString.length() > 0) {
                                if (ChannelCreateActivity.this.searching && ChannelCreateActivity.this.searchWas) {
                                    search = true;
                                }
                                Spannable span = ChannelCreateActivity.this.nameTextView.getText();
                                for (int a = 0; a < ChannelCreateActivity.this.allSpans.size(); a++) {
                                    ChipSpan sp = (ChipSpan) ChannelCreateActivity.this.allSpans.get(a);
                                    if (span.getSpanStart(sp) == -1) {
                                        ChannelCreateActivity.this.allSpans.remove(sp);
                                        ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(sp.uid));
                                    }
                                }
                                ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                                ChannelCreateActivity.this.listView.invalidateViews();
                            } else {
                                search = true;
                            }
                        } else {
                            search = true;
                        }
                        if (search) {
                            String text = ChannelCreateActivity.this.nameTextView.getText().toString().replace("<", "");
                            if (text.length() != 0) {
                                ChannelCreateActivity.this.searching = true;
                                ChannelCreateActivity.this.searchWas = true;
                                if (ChannelCreateActivity.this.listView != null) {
                                    ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.searchListViewAdapter);
                                    ChannelCreateActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                    ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(false);
                                    ChannelCreateActivity.this.listView.setFastScrollEnabled(false);
                                    ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                                }
                                if (ChannelCreateActivity.this.emptyTextView != null) {
                                    ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                                }
                                ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(text);
                                return;
                            }
                            ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                            ChannelCreateActivity.this.searching = false;
                            ChannelCreateActivity.this.searchWas = false;
                            ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
                            ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                            ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                            ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
                            ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                            ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                        }
                    }
                }
            });
            LinearLayout emptyTextLayout = new LinearLayout(context);
            emptyTextLayout.setVisibility(4);
            emptyTextLayout.setOrientation(1);
            this.linearLayout.addView(emptyTextLayout, LayoutHelper.createLinear(-1, -1));
            emptyTextLayout.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            this.emptyTextView = new TextView(context);
            this.emptyTextView.setTextColor(-8355712);
            this.emptyTextView.setTextSize(20.0f);
            this.emptyTextView.setGravity(17);
            this.emptyTextView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
            emptyTextLayout.addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5f));
            emptyTextLayout.addView(new FrameLayout(context), LayoutHelper.createLinear(-1, -1, 0.5f));
            this.listView = new LetterSectionsListView(context);
            this.listView.setEmptyView(emptyTextLayout);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDivider(null);
            this.listView.setDividerHeight(0);
            this.listView.setFastScrollEnabled(true);
            this.listView.setScrollBarStyle(33554432);
            this.listView.setAdapter(this.listViewAdapter);
            this.listView.setFastScrollAlwaysVisible(true);
            this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
            this.linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    User user;
                    if (ChannelCreateActivity.this.searching && ChannelCreateActivity.this.searchWas) {
                        user = (User) ChannelCreateActivity.this.searchListViewAdapter.getItem(i);
                    } else {
                        int section = ChannelCreateActivity.this.listViewAdapter.getSectionForPosition(i);
                        int row = ChannelCreateActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                        if (row >= 0 && section >= 0) {
                            user = (User) ChannelCreateActivity.this.listViewAdapter.getItem(section, row);
                        } else {
                            return;
                        }
                    }
                    if (user != null) {
                        boolean check = true;
                        ChipSpan span;
                        if (ChannelCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(user.id))) {
                            check = false;
                            try {
                                span = (ChipSpan) ChannelCreateActivity.this.selectedContacts.get(Integer.valueOf(user.id));
                                ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(user.id));
                                SpannableStringBuilder text = new SpannableStringBuilder(ChannelCreateActivity.this.nameTextView.getText());
                                text.delete(text.getSpanStart(span), text.getSpanEnd(span));
                                ChannelCreateActivity.this.allSpans.remove(span);
                                ChannelCreateActivity.this.ignoreChange = true;
                                ChannelCreateActivity.this.nameTextView.setText(text);
                                ChannelCreateActivity.this.nameTextView.setSelection(text.length());
                                ChannelCreateActivity.this.ignoreChange = false;
                            } catch (Throwable e) {
                                FileLog.e("tmessages", e);
                            }
                        } else {
                            ChannelCreateActivity.this.ignoreChange = true;
                            span = ChannelCreateActivity.this.createAndPutChipForUser(user);
                            if (span != null) {
                                span.uid = user.id;
                            }
                            ChannelCreateActivity.this.ignoreChange = false;
                            if (span == null) {
                                return;
                            }
                        }
                        ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                        if (ChannelCreateActivity.this.searching || ChannelCreateActivity.this.searchWas) {
                            ChannelCreateActivity.this.ignoreChange = true;
                            SpannableStringBuilder ssb = new SpannableStringBuilder("");
                            Iterator it = ChannelCreateActivity.this.allSpans.iterator();
                            while (it.hasNext()) {
                                ImageSpan sp = (ImageSpan) it.next();
                                ssb.append("<<");
                                ssb.setSpan(sp, ssb.length() - 2, ssb.length(), 33);
                            }
                            ChannelCreateActivity.this.nameTextView.setText(ssb);
                            ChannelCreateActivity.this.nameTextView.setSelection(ssb.length());
                            ChannelCreateActivity.this.ignoreChange = false;
                            ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                            ChannelCreateActivity.this.searching = false;
                            ChannelCreateActivity.this.searchWas = false;
                            ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
                            ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                            ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                            ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
                            ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                            ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                        } else if (view instanceof UserCell) {
                            ((UserCell) view).setChecked(check, true);
                        }
                    }
                }
            });
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    boolean z = true;
                    if (i == 1) {
                        AndroidUtilities.hideKeyboard(ChannelCreateActivity.this.nameTextView);
                    }
                    if (ChannelCreateActivity.this.listViewAdapter != null) {
                        ContactsAdapter access$3200 = ChannelCreateActivity.this.listViewAdapter;
                        if (i == 0) {
                            z = false;
                        }
                        access$3200.setIsScrolling(z);
                    }
                }

                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (absListView.isFastScrollEnabled()) {
                        AndroidUtilities.clearDrawableAnimation(absListView);
                    }
                }
            });
        }
        return this.fragmentView;
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
                                ChannelCreateActivity.this.invite = (ExportedChatInvite) response;
                            }
                            ChannelCreateActivity.this.loadingInvite = false;
                            ChannelCreateActivity.this.privateContainer.setText(ChannelCreateActivity.this.invite != null ? ChannelCreateActivity.this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
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
                this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", R.string.ChannelUsernameHelp));
                this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
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

    public void didUploadedPhoto(final InputFile file, final PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ChannelCreateActivity.this.uploadedAvatar = file;
                ChannelCreateActivity.this.avatar = small.location;
                ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
                if (ChannelCreateActivity.this.createAfterUpload) {
                    try {
                        if (ChannelCreateActivity.this.progressDialog != null && ChannelCreateActivity.this.progressDialog.isShowing()) {
                            ChannelCreateActivity.this.progressDialog.dismiss();
                            ChannelCreateActivity.this.progressDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    ChannelCreateActivity.this.doneButton.performClick();
                }
            }
        });
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
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
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = args.getString("path");
            }
            String text = args.getString("nameTextView");
            if (text == null) {
                return;
            }
            if (this.nameTextView != null) {
                this.nameTextView.setText(text);
            } else {
                this.nameToSet = text;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.currentStep != 1) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.chatDidFailCreate) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
            this.donePressed = false;
        } else if (id == NotificationCenter.chatDidCreated) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
            }
            int chat_id = ((Integer) args[0]).intValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putInt("chat_id", chat_id);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance().changeChatAvatar(chat_id, this.uploadedAvatar);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        } else if (id == NotificationCenter.contactsDidLoaded && this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
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
                            ChannelCreateActivity.this.loadingAdminedChannels = false;
                            if (response != null && ChannelCreateActivity.this.getParentActivity() != null) {
                                int a;
                                for (a = 0; a < ChannelCreateActivity.this.adminedChannelCells.size(); a++) {
                                    ChannelCreateActivity.this.linearLayout.removeView((View) ChannelCreateActivity.this.adminedChannelCells.get(a));
                                }
                                ChannelCreateActivity.this.adminedChannelCells.clear();
                                TL_messages_chats res = response;
                                a = 0;
                                while (a < res.chats.size()) {
                                    AdminedChannelCell adminedChannelCell = new AdminedChannelCell(ChannelCreateActivity.this.getParentActivity(), new View.OnClickListener() {
                                        public void onClick(View view) {
                                            final Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                                            Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            if (channel.megagroup) {
                                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, "telegram.me/" + channel.username, channel.title)));
                                            } else {
                                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, "telegram.me/" + channel.username, channel.title)));
                                            }
                                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                            builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    TL_channels_updateUsername req = new TL_channels_updateUsername();
                                                    req.channel = MessagesController.getInputChannel(channel);
                                                    req.username = "";
                                                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                                        public void run(TLObject response, TL_error error) {
                                                            if (response instanceof TL_boolTrue) {
                                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                                    public void run() {
                                                                        ChannelCreateActivity.this.canCreatePublic = true;
                                                                        if (ChannelCreateActivity.this.nameTextView.length() > 0) {
                                                                            ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                                                                        }
                                                                        ChannelCreateActivity.this.updatePrivatePublic();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }, 64);
                                                }
                                            });
                                            ChannelCreateActivity.this.showDialog(builder.create());
                                        }
                                    });
                                    adminedChannelCell.setChannel((Chat) res.chats.get(a), a == res.chats.size() + -1);
                                    ChannelCreateActivity.this.adminedChannelCells.add(adminedChannelCell);
                                    ChannelCreateActivity.this.linearLayout.addView(adminedChannelCell, ChannelCreateActivity.this.linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, 72));
                                    a++;
                                }
                                ChannelCreateActivity.this.updatePrivatePublic();
                            }
                        }
                    });
                }
            });
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
                if (a == 0 && ch >= '0' && ch <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
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
                    req.channel = MessagesController.getInputChannel(ChannelCreateActivity.this.chatId);
                    ChannelCreateActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ChannelCreateActivity.this.checkReqId = 0;
                                    if (ChannelCreateActivity.this.lastCheckName != null && ChannelCreateActivity.this.lastCheckName.equals(name)) {
                                        if (error == null && (response instanceof TL_boolTrue)) {
                                            ChannelCreateActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                                            ChannelCreateActivity.this.checkTextView.setTextColor(-14248148);
                                            ChannelCreateActivity.this.lastNameAvailable = true;
                                            return;
                                        }
                                        if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                                            ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                                        } else {
                                            ChannelCreateActivity.this.canCreatePublic = false;
                                            ChannelCreateActivity.this.loadAdminedChannels();
                                        }
                                        ChannelCreateActivity.this.checkTextView.setTextColor(-3198928);
                                        ChannelCreateActivity.this.lastNameAvailable = false;
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

    private void showErrorAlert(String error) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            Object obj = -1;
            switch (error.hashCode()) {
                case -141887186:
                    if (error.equals("USERNAMES_UNAVAILABLE")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 288843630:
                    if (error.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (error.equals("USERNAME_OCCUPIED")) {
                        obj = 1;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    builder.setMessage(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    break;
                case 1:
                    builder.setMessage(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                    break;
                case 2:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    private ChipSpan createAndPutChipForUser(User user) {
        try {
            View textView = ((LayoutInflater) ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(R.layout.group_create_bubble, null);
            TextView text = (TextView) textView.findViewById(R.id.bubble_text_view);
            String name = UserObject.getUserName(user);
            if (!(name.length() != 0 || user.phone == null || user.phone.length() == 0)) {
                name = PhoneFormat.getInstance().format("+" + user.phone);
            }
            text.setText(name + ", ");
            int spec = MeasureSpec.makeMeasureSpec(0, 0);
            textView.measure(spec, spec);
            textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
            Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            canvas.translate((float) (-textView.getScrollX()), (float) (-textView.getScrollY()));
            textView.draw(canvas);
            textView.setDrawingCacheEnabled(true);
            Bitmap viewBmp = textView.getDrawingCache().copy(Config.ARGB_8888, true);
            textView.destroyDrawingCache();
            BitmapDrawable bmpDrawable = new BitmapDrawable(b);
            bmpDrawable.setBounds(0, 0, b.getWidth(), b.getHeight());
            SpannableStringBuilder ssb = new SpannableStringBuilder("");
            ChipSpan chipSpan = new ChipSpan(bmpDrawable, 1);
            this.allSpans.add(chipSpan);
            this.selectedContacts.put(Integer.valueOf(user.id), chipSpan);
            Iterator it = this.allSpans.iterator();
            while (it.hasNext()) {
                ImageSpan sp = (ImageSpan) it.next();
                ssb.append("<<");
                ssb.setSpan(sp, ssb.length() - 2, ssb.length(), 33);
            }
            this.nameTextView.setText(ssb);
            this.nameTextView.setSelection(ssb.length());
            return chipSpan;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }
}
