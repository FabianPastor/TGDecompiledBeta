package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int add_button = 1;
    private static final int search_button = 0;
    private ActionBarMenuItem addItem;
    private boolean addingToChannel;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private int chat_id;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private EmptyTextProgressView emptyView;
    private SparseArray<User> ignoreUsers;
    private RecyclerListView listView;
    private ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private boolean returnAsResult;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString = null;

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str, ContactsActivity contactsActivity);
    }

    /* renamed from: org.telegram.ui.ContactsActivity$1 */
    class C21091 extends ActionBarMenuOnItemClick {
        C21091() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ContactsActivity.this.finishFragment();
            } else if (i == 1) {
                ContactsActivity.this.presentFragment(new NewContactActivity());
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity$2 */
    class C21102 extends ActionBarMenuItemSearchListener {
        C21102() {
        }

        public void onSearchExpand() {
            ContactsActivity.this.searching = true;
            if (ContactsActivity.this.addItem != null) {
                ContactsActivity.this.addItem.setVisibility(8);
            }
        }

        public void onSearchCollapse() {
            if (ContactsActivity.this.addItem != null) {
                ContactsActivity.this.addItem.setVisibility(0);
            }
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.this.searching = false;
            ContactsActivity.this.searchWas = false;
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollVisible(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
        }

        public void onTextChanged(EditText editText) {
            if (ContactsActivity.this.searchListViewAdapter != null) {
                editText = editText.getText().toString();
                if (editText.length() != 0) {
                    ContactsActivity.this.searchWas = true;
                    if (ContactsActivity.this.listView != null) {
                        ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                        ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ContactsActivity.this.listView.setFastScrollVisible(false);
                        ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                    if (ContactsActivity.this.emptyView != null) {
                        ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
                    }
                }
                ContactsActivity.this.searchListViewAdapter.searchDialogs(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity$3 */
    class C21113 implements OnItemClickListener {
        C21113() {
        }

        public void onItemClick(View view, int i) {
            User user;
            if (ContactsActivity.this.searching == null || ContactsActivity.this.searchWas == null) {
                view = ContactsActivity.this.listViewAdapter.getSectionForPosition(i);
                i = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                if (i >= 0) {
                    if (view >= null) {
                        if ((ContactsActivity.this.onlyUsers && ContactsActivity.this.chat_id == 0) || view != null) {
                            view = ContactsActivity.this.listViewAdapter.getItem(view, i);
                            if ((view instanceof User) != 0) {
                                user = (User) view;
                                if (ContactsActivity.this.returnAsResult != 0) {
                                    if (ContactsActivity.this.ignoreUsers == 0 || ContactsActivity.this.ignoreUsers.indexOfKey(user.id) < 0) {
                                        ContactsActivity.this.didSelectResult(user, true, null);
                                    } else {
                                        return;
                                    }
                                } else if (ContactsActivity.this.createSecretChat != 0) {
                                    ContactsActivity.this.creatingChat = true;
                                    SecretChatHelper.getInstance(ContactsActivity.this.currentAccount).startSecretChat(ContactsActivity.this.getParentActivity(), user);
                                } else {
                                    i = new Bundle();
                                    i.putInt("user_id", user.id);
                                    if (MessagesController.getInstance(ContactsActivity.this.currentAccount).checkCanOpenChat(i, ContactsActivity.this) != null) {
                                        ContactsActivity.this.presentFragment(new ChatActivity(i), true);
                                    }
                                }
                            } else if ((view instanceof Contact) != 0) {
                                Contact contact = (Contact) view;
                                view = contact.phones.isEmpty() == 0 ? (String) contact.phones.get(0) : null;
                                if (view != null) {
                                    if (ContactsActivity.this.getParentActivity() != 0) {
                                        i = new Builder(ContactsActivity.this.getParentActivity());
                                        i.setMessage(LocaleController.getString("InviteUser", C0446R.string.InviteUser));
                                        i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                        i.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                try {
                                                    dialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", view, null));
                                                    dialogInterface.putExtra("sms_body", ContactsController.getInstance(ContactsActivity.this.currentAccount).getInviteText(1));
                                                    ContactsActivity.this.getParentActivity().startActivityForResult(dialogInterface, 500);
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                            }
                                        });
                                        i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                        ContactsActivity.this.showDialog(i.create());
                                    }
                                }
                                return;
                            }
                        } else if (ContactsActivity.this.needPhonebook != null) {
                            if (i == 0) {
                                ContactsActivity.this.presentFragment(new InviteContactsActivity());
                            }
                        } else if (ContactsActivity.this.chat_id != null) {
                            if (i == 0) {
                                ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                            }
                        } else if (i == 0) {
                            ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                        } else if (i == 1) {
                            view = new Bundle();
                            view.putBoolean("onlyUsers", true);
                            view.putBoolean("destroyAfterSelect", true);
                            view.putBoolean("createSecretChat", true);
                            view.putBoolean("allowBots", false);
                            ContactsActivity.this.presentFragment(new ContactsActivity(view), false);
                        } else if (i == 2) {
                            view = MessagesController.getGlobalMainSettings();
                            if (BuildVars.DEBUG_VERSION != 0 || view.getBoolean("channel_intro", false) == 0) {
                                ContactsActivity.this.presentFragment(new ChannelIntroActivity());
                                view.edit().putBoolean("channel_intro", true).commit();
                            } else {
                                view = new Bundle();
                                view.putInt("step", 0);
                                ContactsActivity.this.presentFragment(new ChannelCreateActivity(view));
                            }
                        }
                    }
                }
                return;
            }
            user = (User) ContactsActivity.this.searchListViewAdapter.getItem(i);
            if (user != null) {
                if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(i) != 0) {
                    i = new ArrayList();
                    i.add(user);
                    MessagesController.getInstance(ContactsActivity.this.currentAccount).putUsers(i, false);
                    MessagesStorage.getInstance(ContactsActivity.this.currentAccount).putUsersAndChats(i, null, false, true);
                }
                if (ContactsActivity.this.returnAsResult != 0) {
                    if (ContactsActivity.this.ignoreUsers == 0 || ContactsActivity.this.ignoreUsers.indexOfKey(user.id) < 0) {
                        ContactsActivity.this.didSelectResult(user, true, null);
                    }
                } else if (ContactsActivity.this.createSecretChat == 0) {
                    i = new Bundle();
                    i.putInt("user_id", user.id);
                    if (MessagesController.getInstance(ContactsActivity.this.currentAccount).checkCanOpenChat(i, ContactsActivity.this) != null) {
                        ContactsActivity.this.presentFragment(new ChatActivity(i), true);
                    }
                } else if (user.id != UserConfig.getInstance(ContactsActivity.this.currentAccount).getClientUserId()) {
                    ContactsActivity.this.creatingChat = true;
                    SecretChatHelper.getInstance(ContactsActivity.this.currentAccount).startSecretChat(ContactsActivity.this.getParentActivity(), user);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity$4 */
    class C21124 extends OnScrollListener {
        C21124() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && ContactsActivity.this.searching != null && ContactsActivity.this.searchWas != null) {
                AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            super.onScrolled(recyclerView, i, i2);
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity$7 */
    class C21137 implements ThemeDescriptionDelegate {
        C21137() {
        }

        public void didSetColor() {
            if (ContactsActivity.this.listView != null) {
                int childCount = ContactsActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ContactsActivity.this.listView.getChildAt(i);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    } else if (childAt instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) childAt).update(0);
                    }
                }
            }
        }
    }

    public ContactsActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        if (this.arguments != null) {
            this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.addingToChannel = this.arguments.getBoolean("addingToChannel", false);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chat_id = this.arguments.getInt("chat_id", 0);
        } else {
            this.needPhonebook = true;
        }
        ContactsController.getInstance(this.currentAccount).checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", C0446R.string.Contacts));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", C0446R.string.SelectContact));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", C0446R.string.NewSecretChat));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", C0446R.string.NewMessageTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C21091());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21102()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        if (!(this.createSecretChat || this.returnAsResult)) {
            this.addItem = createMenu.addItem(1, (int) C0446R.drawable.add);
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, 0);
        this.listViewAdapter = new ContactsAdapter(context, this.onlyUsers, this.needPhonebook, this.ignoreUsers, this.chat_id != 0);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C21113());
        this.listView.setOnScrollListener(new C21124());
        return this.fragmentView;
    }

    private void didSelectResult(final User user, boolean z, String str) {
        if (!z || !this.selectAlertString) {
            if (this.delegate) {
                this.delegate.didSelectContact(user, str, this);
                this.delegate = null;
            }
            if (this.needFinishFragment != null) {
                finishFragment();
            }
        } else if (!getParentActivity()) {
        } else {
            if (user.bot && user.bot_nochats && !this.addingToChannel) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", C0446R.string.BotCantJoinGroups), 0).show();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                return;
            }
            z = new Builder(getParentActivity());
            z.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            CharSequence formatStringSimple = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            if (user.bot || !this.needForwardCount) {
                str = null;
            } else {
                formatStringSimple = String.format("%s\n\n%s", new Object[]{formatStringSimple, LocaleController.getString("AddToTheGroupForwardCount", C0446R.string.AddToTheGroupForwardCount)});
                str = new EditText(getParentActivity());
                str.setTextSize(1, 18.0f);
                str.setText("50");
                str.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                str.setGravity(17);
                str.setInputType(2);
                str.setImeOptions(6);
                str.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                str.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        try {
                            editable = editable.toString();
                            if (editable.length() != 0) {
                                int intValue = Utilities.parseInt(editable).intValue();
                                if (intValue < 0) {
                                    str.setText("0");
                                    str.setSelection(str.length());
                                } else if (intValue > 300) {
                                    str.setText("300");
                                    str.setSelection(str.length());
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(intValue);
                                    if (editable.equals(stringBuilder.toString()) == null) {
                                        editable = str;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                        stringBuilder.append(intValue);
                                        editable.setText(stringBuilder.toString());
                                        str.setSelection(str.length());
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                z.setView(str);
            }
            z.setMessage(formatStringSimple);
            z.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.didSelectResult(user, false, str != null ? str.getText().toString() : "0");
                }
            });
            z.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            showDialog(z.create());
            if (str != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) str.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof LayoutParams) {
                        ((LayoutParams) marginLayoutParams).gravity = 1;
                    }
                    z = AndroidUtilities.dp(true);
                    marginLayoutParams.leftMargin = z;
                    marginLayoutParams.rightMargin = z;
                    marginLayoutParams.height = AndroidUtilities.dp(true);
                    str.setLayoutParams(marginLayoutParams);
                }
                str.setSelection(str.getText().length());
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Context parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                    return;
                }
                if (parentActivity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                    Builder builder = new Builder(parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    builder.setMessage(LocaleController.getString("PermissionContacts", C0446R.string.PermissionContacts));
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                    Dialog create = builder.create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                askForPermissons();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    @TargetApi(23)
    private void askForPermissons() {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                ArrayList arrayList = new ArrayList();
                arrayList.add("android.permission.READ_CONTACTS");
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
                parentActivity.requestPermissions((String[]) arrayList.toArray(new String[arrayList.size()]), 1);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2) {
                    if (iArr[i2] == 0) {
                        String str = strArr[i2];
                        int i3 = -1;
                        if (str.hashCode() == NUM) {
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                i3 = 0;
                            }
                        }
                        if (i3 == 0) {
                            ContactsController.getInstance(this.currentAccount).forceImportContacts();
                        }
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != 0) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0 || (i & 4) != 0) {
                updateVisibleRows(i);
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat != 0 && this.creatingChat != 0) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
                i2 = new Bundle();
                i2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(i2), 1);
            }
        } else if (i == NotificationCenter.closeChats && this.creatingChat == 0) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setIgnoreUsers(SparseArray<User> sparseArray) {
        this.ignoreUsers = sparseArray;
    }

    public ThemeDescription[] getThemeDescriptions() {
        C21137 c21137 = new C21137();
        r11 = new ThemeDescription[36];
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r11[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        r11[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        r11[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        View view = this.listView;
        View view2 = view;
        r11[9] = new ThemeDescription(view2, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r11[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r11[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r11[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        r11[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        r11[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        r11[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = c21137;
        r11[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r11[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r11[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C21137 c211372 = c21137;
        r11[19] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundRed);
        r11[20] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundOrange);
        r11[21] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundViolet);
        r11[22] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundGreen);
        r11[23] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundCyan);
        r11[24] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundBlue);
        r11[25] = new ThemeDescription(null, 0, null, null, null, c211372, Theme.key_avatar_backgroundPink);
        r11[26] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r11[27] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r11[28] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r11[29] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r11[30] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        r11[31] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck);
        r11[32] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground);
        r11[33] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r11[34] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        r11[35] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        return r11;
    }
}
