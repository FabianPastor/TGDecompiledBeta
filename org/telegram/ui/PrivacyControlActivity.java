package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate;
import org.telegram.ui.PrivacyUsersActivity.PrivacyActivityDelegate;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int alwaysShareRow;
    private ArrayList<Integer> currentMinus;
    private ArrayList<Integer> currentPlus;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private boolean enableAnimation;
    private int everybodyRow;
    private int lastCheckedType = -1;
    private ListAdapter listAdapter;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
                return false;
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.alwaysShareRow;
        }

        public int getCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                String value;
                if (i == PrivacyControlActivity.this.alwaysShareRow) {
                    if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                        value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
                    } else {
                        value = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                    }
                    String string;
                    if (PrivacyControlActivity.this.rulesType != 0) {
                        string = LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow);
                        if (PrivacyControlActivity.this.neverShareRow == -1) {
                            z = false;
                        }
                        textCell.setTextAndValue(string, value, z);
                    } else {
                        string = LocaleController.getString("AlwaysShareWith", R.string.AlwaysShareWith);
                        if (PrivacyControlActivity.this.neverShareRow == -1) {
                            z = false;
                        }
                        textCell.setTextAndValue(string, value, z);
                    }
                } else if (i == PrivacyControlActivity.this.neverShareRow) {
                    if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                        value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size());
                    } else {
                        value = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                    }
                    if (PrivacyControlActivity.this.rulesType != 0) {
                        textCell.setTextAndValue(LocaleController.getString("NeverAllow", R.string.NeverAllow), value, false);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("NeverShareWith", R.string.NeverShareWith), value, false);
                    }
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == PrivacyControlActivity.this.detailRow) {
                    if (PrivacyControlActivity.this.rulesType == 2) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("WhoCanCallMeInfo", R.string.WhoCanCallMeInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 1) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                    }
                    view.setBackgroundResource(R.drawable.greydivider);
                } else if (i == PrivacyControlActivity.this.shareDetailRow) {
                    if (PrivacyControlActivity.this.rulesType == 2) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("CustomCallInfo", R.string.CustomCallInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 1) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("CustomShareInfo", R.string.CustomShareInfo));
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("CustomShareSettingsHelp", R.string.CustomShareSettingsHelp));
                    }
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == PrivacyControlActivity.this.sectionRow) {
                    if (PrivacyControlActivity.this.rulesType == 2) {
                        ((HeaderCell) view).setText(LocaleController.getString("WhoCanCallMe", R.string.WhoCanCallMe));
                    } else if (PrivacyControlActivity.this.rulesType == 1) {
                        ((HeaderCell) view).setText(LocaleController.getString("WhoCanAddMe", R.string.WhoCanAddMe));
                    } else {
                        ((HeaderCell) view).setText(LocaleController.getString("LastSeenTitle", R.string.LastSeenTitle));
                    }
                } else if (i == PrivacyControlActivity.this.shareSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("AddExceptions", R.string.AddExceptions));
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new RadioCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                RadioCell textCell2 = (RadioCell) view;
                int checkedType = 0;
                if (i == PrivacyControlActivity.this.everybodyRow) {
                    textCell2.setText(LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                    checkedType = 0;
                } else if (i == PrivacyControlActivity.this.myContactsRow) {
                    String string2 = LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
                    if (PrivacyControlActivity.this.lastCheckedType == 2) {
                        r6 = true;
                    } else {
                        r6 = false;
                    }
                    textCell2.setText(string2, r6, PrivacyControlActivity.this.nobodyRow != -1);
                    checkedType = 2;
                } else if (i == PrivacyControlActivity.this.nobodyRow) {
                    String string3 = LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
                    if (PrivacyControlActivity.this.lastCheckedType == 1) {
                        r6 = true;
                    } else {
                        r6 = false;
                    }
                    textCell2.setText(string3, r6, false);
                    checkedType = 1;
                }
                if (PrivacyControlActivity.this.lastCheckedType == checkedType) {
                    textCell2.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                } else if (PrivacyControlActivity.this.currentType == checkedType) {
                    textCell2.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow) {
                return 3;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 4;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public PrivacyControlActivity(int type) {
        this.rulesType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.privacyRulesUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.privacyRulesUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.rulesType == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        } else if (this.rulesType == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacyControlActivity.this.finishFragment();
                } else if (id == 1 && PrivacyControlActivity.this.getParentActivity() != null) {
                    if (PrivacyControlActivity.this.currentType != 0 && PrivacyControlActivity.this.rulesType == 0) {
                        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        if (!preferences.getBoolean("privacyAlertShowed", false)) {
                            Builder builder = new Builder(PrivacyControlActivity.this.getParentActivity());
                            if (PrivacyControlActivity.this.rulesType == 1) {
                                builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                            } else {
                                builder.setMessage(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                            }
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PrivacyControlActivity.this.applyCurrentPrivacySettings();
                                    preferences.edit().putBoolean("privacyAlertShowed", true).commit();
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            PrivacyControlActivity.this.showDialog(builder.create());
                            return;
                        }
                    }
                    PrivacyControlActivity.this.applyCurrentPrivacySettings();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(8);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView);
        LayoutParams layoutParams = (LayoutParams) listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        listView.setLayoutParams(layoutParams);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                boolean z = true;
                if (i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow) {
                    int newType = PrivacyControlActivity.this.currentType;
                    if (i == PrivacyControlActivity.this.nobodyRow) {
                        newType = 1;
                    } else if (i == PrivacyControlActivity.this.everybodyRow) {
                        newType = 0;
                    } else if (i == PrivacyControlActivity.this.myContactsRow) {
                        newType = 2;
                    }
                    if (newType != PrivacyControlActivity.this.currentType) {
                        PrivacyControlActivity.this.enableAnimation = true;
                        PrivacyControlActivity.this.doneButton.setVisibility(0);
                        PrivacyControlActivity.this.lastCheckedType = PrivacyControlActivity.this.currentType;
                        PrivacyControlActivity.this.currentType = newType;
                        PrivacyControlActivity.this.updateRows();
                    }
                } else if (i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.alwaysShareRow) {
                    ArrayList<Integer> createFromArray;
                    if (i == PrivacyControlActivity.this.neverShareRow) {
                        createFromArray = PrivacyControlActivity.this.currentMinus;
                    } else {
                        createFromArray = PrivacyControlActivity.this.currentPlus;
                    }
                    boolean z2;
                    if (createFromArray.isEmpty()) {
                        Bundle args = new Bundle();
                        args.putBoolean(i == PrivacyControlActivity.this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                        String str = "isGroup";
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        args.putBoolean(str, z2);
                        GroupCreateActivity fragment = new GroupCreateActivity(args);
                        fragment.setDelegate(new GroupCreateActivityDelegate() {
                            public void didSelectUsers(ArrayList<Integer> ids) {
                                int a;
                                if (i == PrivacyControlActivity.this.neverShareRow) {
                                    PrivacyControlActivity.this.currentMinus = ids;
                                    for (a = 0; a < PrivacyControlActivity.this.currentMinus.size(); a++) {
                                        PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(a));
                                    }
                                } else {
                                    PrivacyControlActivity.this.currentPlus = ids;
                                    for (a = 0; a < PrivacyControlActivity.this.currentPlus.size(); a++) {
                                        PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(a));
                                    }
                                }
                                PrivacyControlActivity.this.doneButton.setVisibility(0);
                                PrivacyControlActivity.this.lastCheckedType = -1;
                                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
                            }
                        });
                        PrivacyControlActivity.this.presentFragment(fragment);
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType != 0) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (i != PrivacyControlActivity.this.alwaysShareRow) {
                        z = false;
                    }
                    PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(createFromArray, z2, z);
                    fragment2.setDelegate(new PrivacyActivityDelegate() {
                        public void didUpdatedUserList(ArrayList<Integer> ids, boolean added) {
                            int a;
                            if (i == PrivacyControlActivity.this.neverShareRow) {
                                PrivacyControlActivity.this.currentMinus = ids;
                                if (added) {
                                    for (a = 0; a < PrivacyControlActivity.this.currentMinus.size(); a++) {
                                        PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(a));
                                    }
                                }
                            } else {
                                PrivacyControlActivity.this.currentPlus = ids;
                                if (added) {
                                    for (a = 0; a < PrivacyControlActivity.this.currentPlus.size(); a++) {
                                        PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(a));
                                    }
                                }
                            }
                            PrivacyControlActivity.this.doneButton.setVisibility(0);
                            PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
                        }
                    });
                    PrivacyControlActivity.this.presentFragment(fragment2);
                }
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        }
    }

    private void applyCurrentPrivacySettings() {
        int a;
        User user;
        InputUser inputUser;
        TL_account_setPrivacy req = new TL_account_setPrivacy();
        if (this.rulesType == 2) {
            req.key = new TL_inputPrivacyKeyPhoneCall();
        } else if (this.rulesType == 1) {
            req.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            req.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers rule = new TL_inputPrivacyValueAllowUsers();
            for (a = 0; a < this.currentPlus.size(); a++) {
                user = MessagesController.getInstance().getUser((Integer) this.currentPlus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInputUser(user);
                    if (inputUser != null) {
                        rule.users.add(inputUser);
                    }
                }
            }
            req.rules.add(rule);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers rule2 = new TL_inputPrivacyValueDisallowUsers();
            for (a = 0; a < this.currentMinus.size(); a++) {
                user = MessagesController.getInstance().getUser((Integer) this.currentMinus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInputUser(user);
                    if (inputUser != null) {
                        rule2.users.add(inputUser);
                    }
                }
            }
            req.rules.add(rule2);
        }
        if (this.currentType == 0) {
            req.rules.add(new TL_inputPrivacyValueAllowAll());
        } else if (this.currentType == 1) {
            req.rules.add(new TL_inputPrivacyValueDisallowAll());
        } else if (this.currentType == 2) {
            req.rules.add(new TL_inputPrivacyValueAllowContacts());
        }
        ProgressDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        final ProgressDialog progressDialogFinal = progressDialog;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            if (progressDialogFinal != null) {
                                progressDialogFinal.dismiss();
                            }
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                        if (error == null) {
                            PrivacyControlActivity.this.finishFragment();
                            TL_account_privacyRules rules = response;
                            MessagesController.getInstance().putUsers(rules.users, false);
                            ContactsController.getInstance().setPrivacyRules(rules.rules, PrivacyControlActivity.this.rulesType);
                            return;
                        }
                        PrivacyControlActivity.this.showErrorAlert();
                    }
                });
            }
        }, 2);
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", R.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList<PrivacyRule> privacyRules = ContactsController.getInstance().getPrivacyRules(this.rulesType);
        if (privacyRules.size() == 0) {
            this.currentType = 1;
            return;
        }
        int type = -1;
        for (int a = 0; a < privacyRules.size(); a++) {
            PrivacyRule rule = (PrivacyRule) privacyRules.get(a);
            if (rule instanceof TL_privacyValueAllowUsers) {
                this.currentPlus.addAll(rule.users);
            } else if (rule instanceof TL_privacyValueDisallowUsers) {
                this.currentMinus.addAll(rule.users);
            } else if (rule instanceof TL_privacyValueAllowAll) {
                type = 0;
            } else if (rule instanceof TL_privacyValueDisallowAll) {
                type = 1;
            } else {
                type = 2;
            }
        }
        if (type == 0 || (type == -1 && this.currentMinus.size() > 0)) {
            this.currentType = 0;
        } else if (type == 2 || (type == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
            this.currentType = 2;
        } else if (type == 1 || (type == -1 && this.currentPlus.size() > 0)) {
            this.currentType = 1;
        }
        if (this.doneButton != null) {
            this.doneButton.setVisibility(8);
        }
        updateRows();
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.sectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.everybodyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.myContactsRow = i;
        if (this.rulesType != 0) {
            this.nobodyRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nobodyRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.detailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareSectionRow = i;
        if (this.currentType == 1 || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.alwaysShareRow = i;
        } else {
            this.alwaysShareRow = -1;
        }
        if (this.currentType == 0 || this.currentType == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.neverShareRow = i;
        } else {
            this.neverShareRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareDetailRow = i;
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        this.lastCheckedType = -1;
        this.enableAnimation = false;
    }
}
