package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
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
    private RecyclerListView listView;
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

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyControlActivity$1 */
    class C22411 extends ActionBarMenuOnItemClick {
        C22411() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacyControlActivity.this.finishFragment();
            } else if (i == 1 && PrivacyControlActivity.this.getParentActivity() != 0) {
                if (PrivacyControlActivity.this.currentType != 0 && PrivacyControlActivity.this.rulesType == 0) {
                    i = MessagesController.getGlobalMainSettings();
                    if (!i.getBoolean("privacyAlertShowed", false)) {
                        Builder builder = new Builder(PrivacyControlActivity.this.getParentActivity());
                        if (PrivacyControlActivity.this.rulesType == 1) {
                            builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", C0446R.string.WhoCanAddMeInfo));
                        } else {
                            builder.setMessage(LocaleController.getString("CustomHelp", C0446R.string.CustomHelp));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PrivacyControlActivity.this.applyCurrentPrivacySettings();
                                i.edit().putBoolean("privacyAlertShowed", true).commit();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                        PrivacyControlActivity.this.showDialog(builder.create());
                        return;
                    }
                }
                PrivacyControlActivity.this.applyCurrentPrivacySettings();
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyControlActivity$2 */
    class C22442 implements OnItemClickListener {
        C22442() {
        }

        public void onItemClick(View view, final int i) {
            boolean z = false;
            if (!(i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.everybodyRow)) {
                if (i != PrivacyControlActivity.this.myContactsRow) {
                    if (i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.alwaysShareRow) {
                        if (i == PrivacyControlActivity.this.neverShareRow) {
                            view = PrivacyControlActivity.this.currentMinus;
                        } else {
                            view = PrivacyControlActivity.this.currentPlus;
                        }
                        if (view.isEmpty()) {
                            view = new Bundle();
                            view.putBoolean(i == PrivacyControlActivity.this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                            String str = "isGroup";
                            if (PrivacyControlActivity.this.rulesType != 0) {
                                z = true;
                            }
                            view.putBoolean(str, z);
                            BaseFragment groupCreateActivity = new GroupCreateActivity(view);
                            groupCreateActivity.setDelegate(new GroupCreateActivityDelegate() {
                                public void didSelectUsers(ArrayList<Integer> arrayList) {
                                    if (i == PrivacyControlActivity.this.neverShareRow) {
                                        PrivacyControlActivity.this.currentMinus = arrayList;
                                        for (arrayList = null; arrayList < PrivacyControlActivity.this.currentMinus.size(); arrayList++) {
                                            PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(arrayList));
                                        }
                                    } else {
                                        PrivacyControlActivity.this.currentPlus = arrayList;
                                        for (arrayList = null; arrayList < PrivacyControlActivity.this.currentPlus.size(); arrayList++) {
                                            PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(arrayList));
                                        }
                                    }
                                    PrivacyControlActivity.this.doneButton.setVisibility(0);
                                    PrivacyControlActivity.this.lastCheckedType = -1;
                                    PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
                                }
                            });
                            PrivacyControlActivity.this.presentFragment(groupCreateActivity);
                        } else {
                            boolean z2 = PrivacyControlActivity.this.rulesType != 0;
                            if (i == PrivacyControlActivity.this.alwaysShareRow) {
                                z = true;
                            }
                            BaseFragment privacyUsersActivity = new PrivacyUsersActivity(view, z2, z);
                            privacyUsersActivity.setDelegate(new PrivacyActivityDelegate() {
                                public void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z) {
                                    if (i == PrivacyControlActivity.this.neverShareRow) {
                                        PrivacyControlActivity.this.currentMinus = arrayList;
                                        if (z) {
                                            for (arrayList = null; arrayList < PrivacyControlActivity.this.currentMinus.size(); arrayList++) {
                                                PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(arrayList));
                                            }
                                        }
                                    } else {
                                        PrivacyControlActivity.this.currentPlus = arrayList;
                                        if (z) {
                                            for (arrayList = null; arrayList < PrivacyControlActivity.this.currentPlus.size(); arrayList++) {
                                                PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(arrayList));
                                            }
                                        }
                                    }
                                    PrivacyControlActivity.this.doneButton.setVisibility(0);
                                    PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
                                }
                            });
                            PrivacyControlActivity.this.presentFragment(privacyUsersActivity);
                        }
                    }
                }
            }
            view = PrivacyControlActivity.this.currentType;
            if (i == PrivacyControlActivity.this.nobodyRow) {
                view = 1;
            } else if (i == PrivacyControlActivity.this.everybodyRow) {
                view = null;
            } else if (i == PrivacyControlActivity.this.myContactsRow) {
                view = 2;
            }
            if (view != PrivacyControlActivity.this.currentType) {
                PrivacyControlActivity.this.enableAnimation = true;
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.this.lastCheckedType = PrivacyControlActivity.this.currentType;
                PrivacyControlActivity.this.currentType = view;
                PrivacyControlActivity.this.updateRows();
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (!(viewHolder == PrivacyControlActivity.this.nobodyRow || viewHolder == PrivacyControlActivity.this.everybodyRow || viewHolder == PrivacyControlActivity.this.myContactsRow || viewHolder == PrivacyControlActivity.this.neverShareRow)) {
                if (viewHolder != PrivacyControlActivity.this.alwaysShareRow) {
                    return null;
                }
            }
            return true;
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new RadioCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            boolean z2 = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == PrivacyControlActivity.this.alwaysShareRow) {
                        if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                            i = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
                        } else {
                            i = LocaleController.getString("EmpryUsersPlaceholder", C0446R.string.EmpryUsersPlaceholder);
                        }
                        String string;
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            string = LocaleController.getString("AlwaysAllow", C0446R.string.AlwaysAllow);
                            if (PrivacyControlActivity.this.neverShareRow != -1) {
                                z2 = true;
                            }
                            textSettingsCell.setTextAndValue(string, i, z2);
                            return;
                        }
                        string = LocaleController.getString("AlwaysShareWith", C0446R.string.AlwaysShareWith);
                        if (PrivacyControlActivity.this.neverShareRow != -1) {
                            z2 = true;
                        }
                        textSettingsCell.setTextAndValue(string, i, z2);
                        return;
                    } else if (i == PrivacyControlActivity.this.neverShareRow) {
                        if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                            i = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size());
                        } else {
                            i = LocaleController.getString("EmpryUsersPlaceholder", C0446R.string.EmpryUsersPlaceholder);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NeverAllow", C0446R.string.NeverAllow), i, false);
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NeverShareWith", C0446R.string.NeverShareWith), i, false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == PrivacyControlActivity.this.detailRow) {
                        if (PrivacyControlActivity.this.rulesType == 2) {
                            textInfoPrivacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", C0446R.string.WhoCanCallMeInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            textInfoPrivacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", C0446R.string.WhoCanAddMeInfo));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("CustomHelp", C0446R.string.CustomHelp));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacyControlActivity.this.shareDetailRow) {
                        if (PrivacyControlActivity.this.rulesType == 2) {
                            textInfoPrivacyCell.setText(LocaleController.getString("CustomCallInfo", C0446R.string.CustomCallInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            textInfoPrivacyCell.setText(LocaleController.getString("CustomShareInfo", C0446R.string.CustomShareInfo));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", C0446R.string.CustomShareSettingsHelp));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == PrivacyControlActivity.this.sectionRow) {
                        if (PrivacyControlActivity.this.rulesType == 2) {
                            headerCell.setText(LocaleController.getString("WhoCanCallMe", C0446R.string.WhoCanCallMe));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            headerCell.setText(LocaleController.getString("WhoCanAddMe", C0446R.string.WhoCanAddMe));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("LastSeenTitle", C0446R.string.LastSeenTitle));
                            return;
                        }
                    } else if (i == PrivacyControlActivity.this.shareSectionRow) {
                        headerCell.setText(LocaleController.getString("AddExceptions", C0446R.string.AddExceptions));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    if (i == PrivacyControlActivity.this.everybodyRow) {
                        radioCell.setText(LocaleController.getString("LastSeenEverybody", C0446R.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                    } else {
                        if (i == PrivacyControlActivity.this.myContactsRow) {
                            radioCell.setText(LocaleController.getString("LastSeenContacts", C0446R.string.LastSeenContacts), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                        } else if (i == PrivacyControlActivity.this.nobodyRow) {
                            radioCell.setText(LocaleController.getString("LastSeenNobody", C0446R.string.LastSeenNobody), PrivacyControlActivity.this.lastCheckedType == 1, false);
                            z = true;
                        }
                        if (PrivacyControlActivity.this.lastCheckedType == z) {
                            radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                            return;
                        } else if (PrivacyControlActivity.this.currentType == z) {
                            radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                            return;
                        } else {
                            return;
                        }
                    }
                    z = false;
                    if (PrivacyControlActivity.this.lastCheckedType == z) {
                        radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else if (PrivacyControlActivity.this.currentType == z) {
                        radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i != PrivacyControlActivity.this.alwaysShareRow) {
                if (i != PrivacyControlActivity.this.neverShareRow) {
                    if (i != PrivacyControlActivity.this.shareDetailRow) {
                        if (i != PrivacyControlActivity.this.detailRow) {
                            if (i != PrivacyControlActivity.this.sectionRow) {
                                if (i != PrivacyControlActivity.this.shareSectionRow) {
                                    if (!(i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow)) {
                                        if (i != PrivacyControlActivity.this.nobodyRow) {
                                            return 0;
                                        }
                                    }
                                    return 3;
                                }
                            }
                            return 2;
                        }
                    }
                    return 1;
                }
            }
            return 0;
        }
    }

    public PrivacyControlActivity(int i) {
        this.rulesType = i;
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.rulesType == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", C0446R.string.Calls));
        } else if (this.rulesType == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", C0446R.string.GroupsAndChannels));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", C0446R.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C22411());
        int visibility = this.doneButton != null ? this.doneButton.getVisibility() : 8;
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(visibility);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C22442());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        }
    }

    private void applyCurrentPrivacySettings() {
        int i;
        User user;
        InputUser inputUser;
        TLObject tL_account_setPrivacy = new TL_account_setPrivacy();
        if (this.rulesType == 2) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyPhoneCall();
        } else if (this.rulesType == 1) {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyChatInvite();
        } else {
            tL_account_setPrivacy.key = new TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TL_inputPrivacyValueAllowUsers tL_inputPrivacyValueAllowUsers = new TL_inputPrivacyValueAllowUsers();
            for (i = 0; i < this.currentPlus.size(); i++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentPlus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueAllowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowUsers);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TL_inputPrivacyValueDisallowUsers tL_inputPrivacyValueDisallowUsers = new TL_inputPrivacyValueDisallowUsers();
            for (i = 0; i < this.currentMinus.size(); i++) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) this.currentMinus.get(i));
                if (user != null) {
                    inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                    if (inputUser != null) {
                        tL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowUsers);
        }
        if (this.currentType == 0) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowAll());
        } else if (this.currentType == 1) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueDisallowAll());
        } else if (this.currentType == 2) {
            tL_account_setPrivacy.rules.add(new TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog alertDialog = null;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 1);
            alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        if (tL_error == null) {
                            PrivacyControlActivity.this.finishFragment();
                            TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) tLObject;
                            MessagesController.getInstance(PrivacyControlActivity.this.currentAccount).putUsers(tL_account_privacyRules.users, false);
                            ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).setPrivacyRules(tL_account_privacyRules.rules, PrivacyControlActivity.this.rulesType);
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
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", C0446R.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules != null) {
            if (privacyRules.size() != 0) {
                int i = -1;
                for (int i2 = 0; i2 < privacyRules.size(); i2++) {
                    PrivacyRule privacyRule = (PrivacyRule) privacyRules.get(i2);
                    if (privacyRule instanceof TL_privacyValueAllowUsers) {
                        this.currentPlus.addAll(privacyRule.users);
                    } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                        this.currentMinus.addAll(privacyRule.users);
                    } else {
                        i = privacyRule instanceof TL_privacyValueAllowAll ? 0 : privacyRule instanceof TL_privacyValueDisallowAll ? 1 : 2;
                    }
                }
                if (i != 0) {
                    if (i != -1 || this.currentMinus.size() <= 0) {
                        if (i != 2) {
                            if (i != -1 || this.currentMinus.size() <= 0 || this.currentPlus.size() <= 0) {
                                if (i == 1 || (i == -1 && this.currentPlus.size() > 0)) {
                                    this.currentType = 1;
                                }
                                if (this.doneButton != null) {
                                    this.doneButton.setVisibility(8);
                                }
                                updateRows();
                                return;
                            }
                        }
                        this.currentType = 2;
                        if (this.doneButton != null) {
                            this.doneButton.setVisibility(8);
                        }
                        updateRows();
                        return;
                    }
                }
                this.currentType = 0;
                if (this.doneButton != null) {
                    this.doneButton.setVisibility(8);
                }
                updateRows();
                return;
            }
        }
        this.currentType = 1;
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
        if (this.rulesType == 0 || this.rulesType == 2) {
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
        if (this.currentType != 1) {
            if (this.currentType != 2) {
                this.alwaysShareRow = -1;
                if (this.currentType != 0) {
                    if (this.currentType == 2) {
                        this.neverShareRow = -1;
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.shareDetailRow = i;
                        if (this.listAdapter == null) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.neverShareRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.shareDetailRow = i;
                if (this.listAdapter == null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.alwaysShareRow = i;
        if (this.currentType != 0) {
            if (this.currentType == 2) {
                this.neverShareRow = -1;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.shareDetailRow = i;
                if (this.listAdapter == null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.neverShareRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareDetailRow = i;
        if (this.listAdapter == null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        this.lastCheckedType = -1;
        this.enableAnimation = false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[17];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        return themeDescriptionArr;
    }
}
