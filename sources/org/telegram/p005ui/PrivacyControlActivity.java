package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.RadioCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneP2P;
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

/* renamed from: org.telegram.ui.PrivacyControlActivity */
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
    private RecyclerListView listView;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    /* renamed from: org.telegram.ui.PrivacyControlActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PrivacyControlActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1 && PrivacyControlActivity.this.getParentActivity() != null) {
                if (PrivacyControlActivity.this.currentType != 0 && PrivacyControlActivity.this.rulesType == 0) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (!preferences.getBoolean("privacyAlertShowed", false)) {
                        Builder builder = new Builder(PrivacyControlActivity.this.getParentActivity());
                        if (PrivacyControlActivity.this.rulesType == 1) {
                            builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", CLASSNAMER.string.WhoCanAddMeInfo));
                        } else {
                            builder.setMessage(LocaleController.getString("CustomHelp", CLASSNAMER.string.CustomHelp));
                        }
                        builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new PrivacyControlActivity$1$$Lambda$0(this, preferences));
                        builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                        PrivacyControlActivity.this.showDialog(builder.create());
                        return;
                    }
                }
                PrivacyControlActivity.this.applyCurrentPrivacySettings();
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$PrivacyControlActivity$1(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
            PrivacyControlActivity.this.applyCurrentPrivacySettings();
            preferences.edit().putBoolean("privacyAlertShowed", true).commit();
        }
    }

    /* renamed from: org.telegram.ui.PrivacyControlActivity$LinkMovementMethodMy */
    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.m13e(e);
                return false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyControlActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.alwaysShareRow;
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new RadioCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    String value;
                    if (position == PrivacyControlActivity.this.alwaysShareRow) {
                        if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                            value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
                        } else {
                            value = LocaleController.getString("EmpryUsersPlaceholder", CLASSNAMER.string.EmpryUsersPlaceholder);
                        }
                        String string;
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            string = LocaleController.getString("AlwaysAllow", CLASSNAMER.string.AlwaysAllow);
                            if (PrivacyControlActivity.this.neverShareRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string, value, z);
                            return;
                        }
                        string = LocaleController.getString("AlwaysShareWith", CLASSNAMER.string.AlwaysShareWith);
                        if (PrivacyControlActivity.this.neverShareRow == -1) {
                            z = false;
                        }
                        textCell.setTextAndValue(string, value, z);
                        return;
                    } else if (position == PrivacyControlActivity.this.neverShareRow) {
                        if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                            value = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size());
                        } else {
                            value = LocaleController.getString("EmpryUsersPlaceholder", CLASSNAMER.string.EmpryUsersPlaceholder);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            textCell.setTextAndValue(LocaleController.getString("NeverAllow", CLASSNAMER.string.NeverAllow), value, false);
                            return;
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("NeverShareWith", CLASSNAMER.string.NeverShareWith), value, false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == PrivacyControlActivity.this.detailRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", CLASSNAMER.string.PrivacyCallsP2PHelp));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", CLASSNAMER.string.WhoCanCallMeInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", CLASSNAMER.string.WhoCanAddMeInfo));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomHelp", CLASSNAMER.string.CustomHelp));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacyControlActivity.this.shareDetailRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("CustomP2PInfo", CLASSNAMER.string.CustomP2PInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("CustomCallInfo", CLASSNAMER.string.CustomCallInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("CustomShareInfo", CLASSNAMER.string.CustomShareInfo));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", CLASSNAMER.string.CustomShareSettingsHelp));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == PrivacyControlActivity.this.sectionRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            headerCell.setText(LocaleController.getString("P2PEnabledWith", CLASSNAMER.string.P2PEnabledWith));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            headerCell.setText(LocaleController.getString("WhoCanCallMe", CLASSNAMER.string.WhoCanCallMe));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            headerCell.setText(LocaleController.getString("WhoCanAddMe", CLASSNAMER.string.WhoCanAddMe));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("LastSeenTitle", CLASSNAMER.string.LastSeenTitle));
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.shareSectionRow) {
                        headerCell.setText(LocaleController.getString("AddExceptions", CLASSNAMER.string.AddExceptions));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    RadioCell radioCell = holder.itemView;
                    int checkedType = 0;
                    boolean z2;
                    if (position == PrivacyControlActivity.this.everybodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            radioCell.setText(LocaleController.getString("P2PEverybody", CLASSNAMER.string.P2PEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenEverybody", CLASSNAMER.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        }
                        checkedType = 0;
                    } else if (position == PrivacyControlActivity.this.myContactsRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            String string2 = LocaleController.getString("P2PContacts", CLASSNAMER.string.P2PContacts);
                            if (PrivacyControlActivity.this.lastCheckedType == 2) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            radioCell.setText(string2, z2, PrivacyControlActivity.this.nobodyRow != -1);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenContacts", CLASSNAMER.string.LastSeenContacts), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                        }
                        checkedType = 2;
                    } else if (position == PrivacyControlActivity.this.nobodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            String string3 = LocaleController.getString("P2PNobody", CLASSNAMER.string.P2PNobody);
                            if (PrivacyControlActivity.this.lastCheckedType == 1) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            radioCell.setText(string3, z2, false);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenNobody", CLASSNAMER.string.LastSeenNobody), PrivacyControlActivity.this.lastCheckedType == 1, false);
                        }
                        checkedType = 1;
                    }
                    if (PrivacyControlActivity.this.lastCheckedType == checkedType) {
                        radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else if (PrivacyControlActivity.this.currentType == checkedType) {
                        radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyControlActivity.this.alwaysShareRow || position == PrivacyControlActivity.this.neverShareRow) {
                return 0;
            }
            if (position == PrivacyControlActivity.this.shareDetailRow || position == PrivacyControlActivity.this.detailRow) {
                return 1;
            }
            if (position == PrivacyControlActivity.this.sectionRow || position == PrivacyControlActivity.this.shareSectionRow) {
                return 2;
            }
            if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow) {
                return 3;
            }
            return 0;
        }
    }

    public PrivacyControlActivity(int type) {
        this.rulesType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.rulesType == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", CLASSNAMER.string.PrivacyP2P));
        } else if (this.rulesType == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", CLASSNAMER.string.Calls));
        } else if (this.rulesType == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", CLASSNAMER.string.GroupsAndChannels));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", CLASSNAMER.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        int visibility = this.doneButton != null ? this.doneButton.getVisibility() : 8;
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, CLASSNAMER.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        this.doneButton.setVisibility(visibility);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new PrivacyControlActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int position) {
        boolean z = true;
        if (position == this.nobodyRow || position == this.everybodyRow || position == this.myContactsRow) {
            int newType = this.currentType;
            if (position == this.nobodyRow) {
                newType = 1;
            } else if (position == this.everybodyRow) {
                newType = 0;
            } else if (position == this.myContactsRow) {
                newType = 2;
            }
            if (newType != this.currentType) {
                this.enableAnimation = true;
                this.doneButton.setVisibility(0);
                this.lastCheckedType = this.currentType;
                this.currentType = newType;
                updateRows();
            }
        } else if (position == this.neverShareRow || position == this.alwaysShareRow) {
            ArrayList<Integer> createFromArray;
            if (position == this.neverShareRow) {
                createFromArray = this.currentMinus;
            } else {
                createFromArray = this.currentPlus;
            }
            boolean z2;
            if (createFromArray.isEmpty()) {
                Bundle args = new Bundle();
                args.putBoolean(position == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                String str = "isGroup";
                if (this.rulesType != 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                args.putBoolean(str, z2);
                GroupCreateActivity fragment = new GroupCreateActivity(args);
                fragment.setDelegate(new PrivacyControlActivity$$Lambda$3(this, position));
                presentFragment(fragment);
                return;
            }
            if (this.rulesType != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            if (position != this.alwaysShareRow) {
                z = false;
            }
            PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(createFromArray, z2, z);
            fragment2.setDelegate(new PrivacyControlActivity$$Lambda$4(this, position));
            presentFragment(fragment2);
        }
    }

    final /* synthetic */ void lambda$null$0$PrivacyControlActivity(int position, ArrayList ids) {
        int a;
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            for (a = 0; a < this.currentMinus.size(); a++) {
                this.currentPlus.remove(this.currentMinus.get(a));
            }
        } else {
            this.currentPlus = ids;
            for (a = 0; a < this.currentPlus.size(); a++) {
                this.currentMinus.remove(this.currentPlus.get(a));
            }
        }
        this.doneButton.setVisibility(0);
        this.lastCheckedType = -1;
        this.listAdapter.notifyDataSetChanged();
    }

    final /* synthetic */ void lambda$null$1$PrivacyControlActivity(int position, ArrayList ids, boolean added) {
        int a;
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            if (added) {
                for (a = 0; a < this.currentMinus.size(); a++) {
                    this.currentPlus.remove(this.currentMinus.get(a));
                }
            }
        } else {
            this.currentPlus = ids;
            if (added) {
                for (a = 0; a < this.currentPlus.size(); a++) {
                    this.currentMinus.remove(this.currentPlus.get(a));
                }
            }
        }
        this.doneButton.setVisibility(0);
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        }
    }

    private void applyCurrentPrivacySettings() {
        int a;
        User user;
        InputUser inputUser;
        TL_account_setPrivacy req = new TL_account_setPrivacy();
        if (this.rulesType == 3) {
            req.key = new TL_inputPrivacyKeyPhoneP2P();
        } else if (this.rulesType == 2) {
            req.key = new TL_inputPrivacyKeyPhoneCall();
        } else if (this.rulesType == 1) {
            req.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            req.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers rule = new TL_inputPrivacyValueAllowUsers();
            for (a = 0; a < this.currentPlus.size(); a++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentPlus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
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
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentMinus.get(a));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
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
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PrivacyControlActivity$$Lambda$1(this, progressDialog), 2);
    }

    final /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(AlertDialog progressDialogFinal, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$Lambda$2(this, progressDialogFinal, error, response));
    }

    final /* synthetic */ void lambda$null$3$PrivacyControlActivity(AlertDialog progressDialogFinal, TL_error error, TLObject response) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        if (error == null) {
            lambda$checkDiscard$70$PassportActivity();
            TL_account_privacyRules rules = (TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(rules.users, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(rules.rules, this.rulesType);
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", CLASSNAMER.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList<PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
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
        if (this.rulesType == 0 || this.rulesType == 2 || this.rulesType == 3) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nobodyRow = i;
        } else {
            this.nobodyRow = -1;
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

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[17];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r9[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        return r9;
    }
}
