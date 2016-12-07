package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LetterSectionsListView;

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int add_button = 1;
    private static final int search_button = 0;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private int chat_id;
    private boolean createSecretChat;
    private boolean creatingChat = false;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private TextView emptyTextView;
    private HashMap<Integer, User> ignoreUsers;
    private LetterSectionsListView listView;
    private BaseSectionsAdapter listViewAdapter;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private boolean returnAsResult;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString = null;

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str);
    }

    public ContactsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        if (this.arguments != null) {
            this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.chat_id = this.arguments.getInt("chat_id", 0);
        } else {
            this.needPhonebook = true;
        }
        ContactsController.getInstance().checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public View createView(Context context) {
        boolean z;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", R.string.Contacts));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", R.string.NewSecretChat));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", R.string.NewMessageTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ContactsActivity.this.finishFragment();
                } else if (id == 1) {
                    ContactsActivity.this.presentFragment(new NewContactActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                ContactsActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
                ContactsActivity.this.searching = false;
                ContactsActivity.this.searchWas = false;
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollAlwaysVisible(true);
                ContactsActivity.this.listView.setFastScrollEnabled(true);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
            }

            public void onTextChanged(EditText editText) {
                if (ContactsActivity.this.searchListViewAdapter != null) {
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        ContactsActivity.this.searchWas = true;
                        if (ContactsActivity.this.listView != null) {
                            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                            ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                            ContactsActivity.this.listView.setFastScrollAlwaysVisible(false);
                            ContactsActivity.this.listView.setFastScrollEnabled(false);
                            ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        if (ContactsActivity.this.emptyTextView != null) {
                            ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        }
                    }
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
                }
            }
        }).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        menu.addItem(1, (int) R.drawable.add);
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        int i = this.onlyUsers ? 1 : 0;
        boolean z2 = this.needPhonebook;
        HashMap hashMap = this.ignoreUsers;
        if (this.chat_id != 0) {
            z = true;
        } else {
            z = false;
        }
        this.listViewAdapter = new ContactsAdapter(context, i, z2, hashMap, z);
        this.fragmentView = new FrameLayout(context);
        LinearLayout emptyTextLayout = new LinearLayout(context);
        emptyTextLayout.setVisibility(4);
        emptyTextLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(emptyTextLayout);
        LayoutParams layoutParams = (LayoutParams) emptyTextLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        emptyTextLayout.setLayoutParams(layoutParams);
        emptyTextLayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(1, 20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        emptyTextLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams1.width = -1;
        layoutParams1.height = -1;
        layoutParams1.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams1);
        FrameLayout frameLayout = new FrameLayout(context);
        emptyTextLayout.addView(frameLayout);
        layoutParams1 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams1.width = -1;
        layoutParams1.height = -1;
        layoutParams1.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams1);
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
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user;
                Bundle args;
                if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                    user = (User) ContactsActivity.this.searchListViewAdapter.getItem(i);
                    if (user != null) {
                        if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(i)) {
                            ArrayList<User> users = new ArrayList();
                            users.add(user);
                            MessagesController.getInstance().putUsers(users, false);
                            MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        }
                        if (ContactsActivity.this.returnAsResult) {
                            if (ContactsActivity.this.ignoreUsers == null || !ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                                ContactsActivity.this.didSelectResult(user, true, null);
                                return;
                            }
                            return;
                        } else if (ContactsActivity.this.createSecretChat) {
                            if (user.id != UserConfig.getClientUserId()) {
                                ContactsActivity.this.creatingChat = true;
                                SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), user);
                                return;
                            }
                            return;
                        } else {
                            args = new Bundle();
                            args.putInt("user_id", user.id);
                            if (MessagesController.checkCanOpenChat(args, ContactsActivity.this)) {
                                ContactsActivity.this.presentFragment(new ChatActivity(args), true);
                                return;
                            }
                            return;
                        }
                    }
                    return;
                }
                int section = ContactsActivity.this.listViewAdapter.getSectionForPosition(i);
                int row = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                if (row >= 0 && section >= 0) {
                    if ((ContactsActivity.this.onlyUsers && ContactsActivity.this.chat_id == 0) || section != 0) {
                        Contact item = ContactsActivity.this.listViewAdapter.getItem(section, row);
                        if (item instanceof User) {
                            user = (User) item;
                            if (ContactsActivity.this.returnAsResult) {
                                if (ContactsActivity.this.ignoreUsers == null || !ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                                    ContactsActivity.this.didSelectResult(user, true, null);
                                }
                            } else if (ContactsActivity.this.createSecretChat) {
                                ContactsActivity.this.creatingChat = true;
                                SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), user);
                            } else {
                                args = new Bundle();
                                args.putInt("user_id", user.id);
                                if (MessagesController.checkCanOpenChat(args, ContactsActivity.this)) {
                                    ContactsActivity.this.presentFragment(new ChatActivity(args), true);
                                }
                            }
                        } else if (item instanceof Contact) {
                            Contact contact = item;
                            String usePhone = null;
                            if (!contact.phones.isEmpty()) {
                                usePhone = (String) contact.phones.get(0);
                            }
                            if (usePhone != null && ContactsActivity.this.getParentActivity() != null) {
                                Builder builder = new Builder(ContactsActivity.this.getParentActivity());
                                builder.setMessage(LocaleController.getString("InviteUser", R.string.InviteUser));
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                final String arg1 = usePhone;
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, null));
                                            intent.putExtra("sms_body", LocaleController.getString("InviteText", R.string.InviteText));
                                            ContactsActivity.this.getParentActivity().startActivityForResult(intent, 500);
                                        } catch (Throwable e) {
                                            FileLog.e("tmessages", e);
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                ContactsActivity.this.showDialog(builder.create());
                            }
                        }
                    } else if (ContactsActivity.this.needPhonebook) {
                        if (row == 0) {
                            try {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("text/plain");
                                intent.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                                ContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteFriends", R.string.InviteFriends)), 500);
                            } catch (Throwable e) {
                                FileLog.e("tmessages", e);
                            }
                        }
                    } else if (ContactsActivity.this.chat_id != 0) {
                        if (row == 0) {
                            ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                        }
                    } else if (row == 0) {
                        if (MessagesController.isFeatureEnabled("chat_create", ContactsActivity.this)) {
                            ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                        }
                    } else if (row == 1) {
                        args = new Bundle();
                        args.putBoolean("onlyUsers", true);
                        args.putBoolean("destroyAfterSelect", true);
                        args.putBoolean("createSecretChat", true);
                        args.putBoolean("allowBots", false);
                        ContactsActivity.this.presentFragment(new ContactsActivity(args), false);
                    } else if (row == 2 && MessagesController.isFeatureEnabled("broadcast_create", ContactsActivity.this)) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        if (preferences.getBoolean("channel_intro", false)) {
                            args = new Bundle();
                            args.putInt("step", 0);
                            ContactsActivity.this.presentFragment(new ChannelCreateActivity(args));
                            return;
                        }
                        ContactsActivity.this.presentFragment(new ChannelIntroActivity());
                        preferences.edit().putBoolean("channel_intro", true).commit();
                    }
                }
            }
        });
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 1 && ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (absListView.isFastScrollEnabled()) {
                    AndroidUtilities.clearDrawableAnimation(absListView);
                }
            }
        });
        return this.fragmentView;
    }

    private void didSelectResult(final User user, boolean useAlert, String param) {
        if (!useAlert || this.selectAlertString == null) {
            if (this.delegate != null) {
                this.delegate.didSelectContact(user, param);
                this.delegate = null;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            if (user.bot && user.bot_nochats) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                    return;
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                    return;
                }
            }
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            EditText editText = null;
            if (!user.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", new Object[]{message, LocaleController.getString("AddToTheGroupForwardCount", R.string.AddToTheGroupForwardCount)});
                editText = new EditText(getParentActivity());
                editText.setTextSize(18.0f);
                editText.setText("50");
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                final EditText editTextFinal = editText;
                editText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        try {
                            String str = s.toString();
                            if (str.length() != 0) {
                                int value = Utilities.parseInt(str).intValue();
                                if (value < 0) {
                                    editTextFinal.setText("0");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (value > 300) {
                                    editTextFinal.setText("300");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (!str.equals("" + value)) {
                                    editTextFinal.setText("" + value);
                                    editTextFinal.setSelection(editTextFinal.length());
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                });
                builder.setView(editText);
            }
            builder.setMessage(message);
            final EditText finalEditText = editText;
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0");
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof LayoutParams) {
                        ((LayoutParams) layoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(10.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = args[0];
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
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

    public void setDelegate(ContactsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreUsers(HashMap<Integer, User> users) {
        this.ignoreUsers = users;
    }
}
