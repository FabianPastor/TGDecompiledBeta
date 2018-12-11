package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_contacts_toggleTopPeers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_clearAllDrafts;
import org.telegram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;

/* renamed from: org.telegram.ui.PrivacySettingsActivity */
public class PrivacySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int advancedSectionRow;
    private int blockedRow;
    private int botsDetailRow;
    private int botsSectionRow;
    private int callsRow;
    private boolean[] clear = new boolean[2];
    private int clearDraftsRow;
    private int contactsDeleteRow;
    private int contactsDetailRow;
    private int contactsSectionRow;
    private int contactsSuggestRow;
    private int contactsSyncRow;
    private boolean currentSuggest;
    private boolean currentSync;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean newSuggest;
    private boolean newSync;
    private int p2pRow;
    private int passcodeRow;
    private int passportRow;
    private int passwordRow;
    private int paymentsClearRow;
    private int privacySectionRow;
    private AlertDialog progressDialog;
    private int rowCount;
    private int secretDetailRow;
    private int secretMapRow;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;
    private int webSessionsRow;

    /* renamed from: org.telegram.ui.PrivacySettingsActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PrivacySettingsActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacySettingsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.webSessionsRow || position == PrivacySettingsActivity.this.clearDraftsRow || ((position == PrivacySettingsActivity.this.groupsRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(1)) || ((position == PrivacySettingsActivity.this.lastSeenRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(0)) || ((position == PrivacySettingsActivity.this.callsRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(2)) || ((position == PrivacySettingsActivity.this.p2pRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(3)) || ((position == PrivacySettingsActivity.this.deleteAccountRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo()) || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.contactsDeleteRow || position == PrivacySettingsActivity.this.contactsSuggestRow)))))) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
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
                    view = new TextCheckCell(this.mContext);
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
                    if (position == PrivacySettingsActivity.this.blockedRow) {
                        textCell.setText(LocaleController.getString("BlockedUsers", CLASSNAMER.string.BlockedUsers), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.sessionsRow) {
                        textCell.setText(LocaleController.getString("SessionsTitle", CLASSNAMER.string.SessionsTitle), false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.webSessionsRow) {
                        textCell.setText(LocaleController.getString("WebSessionsTitle", CLASSNAMER.string.WebSessionsTitle), false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.passwordRow) {
                        textCell.setText(LocaleController.getString("TwoStepVerification", CLASSNAMER.string.TwoStepVerification), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.passcodeRow) {
                        textCell.setText(LocaleController.getString("Passcode", CLASSNAMER.string.Passcode), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.lastSeenRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(0)) {
                            value = LocaleController.getString("Loading", CLASSNAMER.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(0);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", CLASSNAMER.string.PrivacyLastSeen), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.groupsRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(1)) {
                            value = LocaleController.getString("Loading", CLASSNAMER.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(1);
                        }
                        textCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", CLASSNAMER.string.GroupsAndChannels), value, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.callsRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(2)) {
                            value = LocaleController.getString("Loading", CLASSNAMER.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(2);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Calls", CLASSNAMER.string.Calls), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.p2pRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingPrivicyInfo(3)) {
                            value = LocaleController.getString("Loading", CLASSNAMER.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(3);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyP2P", CLASSNAMER.string.PrivacyP2P), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.passportRow) {
                        textCell.setText(LocaleController.getString("TelegramPassport", CLASSNAMER.string.TelegramPassport), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo()) {
                            value = LocaleController.getString("Loading", CLASSNAMER.string.Loading);
                        } else {
                            int ttl = ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getDeleteAccountTTL();
                            if (ttl <= 182) {
                                value = LocaleController.formatPluralString("Months", ttl / 30);
                            } else if (ttl == 365) {
                                value = LocaleController.formatPluralString("Years", ttl / 365);
                            } else {
                                value = LocaleController.formatPluralString("Days", ttl);
                            }
                        }
                        textCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor2", CLASSNAMER.string.DeleteAccountIfAwayFor2), value, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.clearDraftsRow) {
                        textCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", CLASSNAMER.string.PrivacyDeleteCloudDrafts), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.paymentsClearRow) {
                        textCell.setText(LocaleController.getString("PrivacyPaymentsClear", CLASSNAMER.string.PrivacyPaymentsClear), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretMapRow) {
                        switch (SharedConfig.mapPreviewType) {
                            case 0:
                                value = LocaleController.getString("MapPreviewProviderTelegram", CLASSNAMER.string.MapPreviewProviderTelegram);
                                break;
                            case 1:
                                value = LocaleController.getString("MapPreviewProviderGoogle", CLASSNAMER.string.MapPreviewProviderGoogle);
                                break;
                            default:
                                value = LocaleController.getString("MapPreviewProviderNobody", CLASSNAMER.string.MapPreviewProviderNobody);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", CLASSNAMER.string.MapPreviewProvider), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsDeleteRow) {
                        textCell.setText(LocaleController.getString("SyncContactsDelete", CLASSNAMER.string.SyncContactsDelete), true);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                        privacyCell.setText(LocaleController.getString("DeleteAccountHelp", CLASSNAMER.string.DeleteAccountHelp));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.groupsDetailRow) {
                        privacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", CLASSNAMER.string.GroupsAndChannelsHelp));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.sessionsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SessionsInfo", CLASSNAMER.string.SessionsInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretDetailRow) {
                        privacyCell.setText(LocaleController.getString("SecretWebPageInfo", CLASSNAMER.string.SecretWebPageInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.botsDetailRow) {
                        privacyCell.setText(LocaleController.getString("PrivacyBotsInfo", CLASSNAMER.string.PrivacyBotsInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SuggestContactsInfo", CLASSNAMER.string.SuggestContactsInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.privacySectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", CLASSNAMER.string.PrivacyTitle));
                        return;
                    } else if (position == PrivacySettingsActivity.this.securitySectionRow) {
                        headerCell.setText(LocaleController.getString("SecurityTitle", CLASSNAMER.string.SecurityTitle));
                        return;
                    } else if (position == PrivacySettingsActivity.this.advancedSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyAdvanced", CLASSNAMER.string.PrivacyAdvanced));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretSectionRow) {
                        headerCell.setText(LocaleController.getString("SecretChat", CLASSNAMER.string.SecretChat));
                        return;
                    } else if (position == PrivacySettingsActivity.this.botsSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyBots", CLASSNAMER.string.PrivacyBots));
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSectionRow) {
                        headerCell.setText(LocaleController.getString("Contacts", CLASSNAMER.string.Contacts));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCheckCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.secretWebpageRow) {
                        String string = LocaleController.getString("SecretWebPage", CLASSNAMER.string.SecretWebPage);
                        if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview != 1) {
                            z = false;
                        }
                        textCheckCell.setTextAndCheck(string, z, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSyncRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", CLASSNAMER.string.SyncContacts), PrivacySettingsActivity.this.newSync, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSuggestRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", CLASSNAMER.string.SuggestContacts), PrivacySettingsActivity.this.newSuggest, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.lastSeenRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.deleteAccountRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.webSessionsRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.groupsRow || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsDeleteRow || position == PrivacySettingsActivity.this.clearDraftsRow) {
                return 0;
            }
            if (position == PrivacySettingsActivity.this.deleteAccountDetailRow || position == PrivacySettingsActivity.this.groupsDetailRow || position == PrivacySettingsActivity.this.sessionsDetailRow || position == PrivacySettingsActivity.this.secretDetailRow || position == PrivacySettingsActivity.this.botsDetailRow || position == PrivacySettingsActivity.this.contactsDetailRow) {
                return 1;
            }
            if (position == PrivacySettingsActivity.this.securitySectionRow || position == PrivacySettingsActivity.this.advancedSectionRow || position == PrivacySettingsActivity.this.privacySectionRow || position == PrivacySettingsActivity.this.secretSectionRow || position == PrivacySettingsActivity.this.botsSectionRow || position == PrivacySettingsActivity.this.contactsSectionRow) {
                return 2;
            }
            if (position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.contactsSuggestRow) {
                return 3;
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        boolean z = UserConfig.getInstance(this.currentAccount).syncContacts;
        this.newSync = z;
        this.currentSync = z;
        z = UserConfig.getInstance(this.currentAccount).suggestContacts;
        this.newSuggest = z;
        this.currentSuggest = z;
        updateRows();
        loadPasswordSettings();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        if (this.currentSync != this.newSync) {
            UserConfig.getInstance(this.currentAccount).syncContacts = this.newSync;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            if (this.newSync) {
                ContactsController.getInstance(this.currentAccount).forceImportContacts();
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", CLASSNAMER.string.SyncContactsAdded), 0).show();
                }
            }
        }
        if (this.newSuggest != this.currentSuggest) {
            if (!this.newSuggest) {
                DataQuery.getInstance(this.currentAccount).clearTopPeers();
            }
            UserConfig.getInstance(this.currentAccount).suggestContacts = this.newSuggest;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            TL_contacts_toggleTopPeers req = new TL_contacts_toggleTopPeers();
            req.enabled = this.newSuggest;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, PrivacySettingsActivity$$Lambda$0.$instance);
        }
    }

    static final /* synthetic */ void lambda$onFragmentDestroy$0$PrivacySettingsActivity(TLObject response, TL_error error) {
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", CLASSNAMER.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new PrivacySettingsActivity$$Lambda$1(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$17$PrivacySettingsActivity(View view, int position) {
        if (!view.isEnabled()) {
            return;
        }
        Builder builder;
        if (position == this.blockedRow) {
            presentFragment(new BlockedUsersActivity());
        } else if (position == this.sessionsRow) {
            presentFragment(new SessionsActivity(0));
        } else if (position == this.webSessionsRow) {
            presentFragment(new SessionsActivity(1));
        } else if (position == this.clearDraftsRow) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
            builder.setMessage(LocaleController.getString("AreYouSureClearDrafts", CLASSNAMER.string.AreYouSureClearDrafts));
            builder.setPositiveButton(LocaleController.getString("Delete", CLASSNAMER.string.Delete), new PrivacySettingsActivity$$Lambda$4(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
            showDialog(builder.create());
        } else if (position == this.deleteAccountRow) {
            if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteAccountTitle", CLASSNAMER.string.DeleteAccountTitle));
                builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Months", 1), LocaleController.formatPluralString("Months", 3), LocaleController.formatPluralString("Months", 6), LocaleController.formatPluralString("Years", 1)}, new PrivacySettingsActivity$$Lambda$5(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                showDialog(builder.create());
            }
        } else if (position == this.lastSeenRow) {
            presentFragment(new PrivacyControlActivity(0));
        } else if (position == this.groupsRow) {
            presentFragment(new PrivacyControlActivity(1));
        } else if (position == this.callsRow) {
            presentFragment(new PrivacyControlActivity(2));
        } else if (position == this.p2pRow) {
            presentFragment(new PrivacyControlActivity(3));
        } else if (position == this.passwordRow) {
            presentFragment(new TwoStepVerificationActivity(0));
        } else if (position == this.passcodeRow) {
            if (SharedConfig.passcodeHash.length() > 0) {
                presentFragment(new PasscodeActivity(2));
            } else {
                presentFragment(new PasscodeActivity(0));
            }
        } else if (position == this.secretWebpageRow) {
            if (MessagesController.getInstance(this.currentAccount).secretWebpagePreview == 1) {
                MessagesController.getInstance(this.currentAccount).secretWebpagePreview = 0;
            } else {
                MessagesController.getInstance(this.currentAccount).secretWebpagePreview = 1;
            }
            MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", MessagesController.getInstance(this.currentAccount).secretWebpagePreview).commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(MessagesController.getInstance(this.currentAccount).secretWebpagePreview == 1);
            }
        } else if (position == this.contactsDeleteRow) {
            if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("Contacts", CLASSNAMER.string.Contacts));
                builder.setMessage(LocaleController.getString("SyncContactsDeleteInfo", CLASSNAMER.string.SyncContactsDeleteInfo));
                builder.setPositiveButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                builder.setNegativeButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new PrivacySettingsActivity$$Lambda$6(this));
                showDialog(builder.create());
            }
        } else if (position == this.contactsSuggestRow) {
            TextCheckCell cell = (TextCheckCell) view;
            if (this.newSuggest) {
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                builder.setMessage(LocaleController.getString("SuggestContactsAlert", CLASSNAMER.string.SuggestContactsAlert));
                builder.setPositiveButton(LocaleController.getString("MuteDisable", CLASSNAMER.string.MuteDisable), new PrivacySettingsActivity$$Lambda$7(this, cell));
                builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                showDialog(builder.create());
                return;
            }
            this.newSuggest = !this.newSuggest;
            cell.setChecked(this.newSuggest);
        } else if (position == this.contactsSyncRow) {
            this.newSync = !this.newSync;
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(this.newSync);
            }
        } else if (position == this.secretMapRow) {
            AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new PrivacySettingsActivity$$Lambda$8(this), false);
        } else if (position == this.paymentsClearRow) {
            BottomSheet.Builder builder2 = new BottomSheet.Builder(getParentActivity());
            builder2.setApplyTopPadding(false);
            builder2.setApplyBottomPadding(false);
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(1);
            for (int a = 0; a < 2; a++) {
                String name = null;
                if (a == 0) {
                    name = LocaleController.getString("PrivacyClearShipping", CLASSNAMER.string.PrivacyClearShipping);
                } else if (a == 1) {
                    name = LocaleController.getString("PrivacyClearPayment", CLASSNAMER.string.PrivacyClearPayment);
                }
                this.clear[a] = true;
                CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                checkBoxCell.setTag(Integer.valueOf(a));
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                checkBoxCell.setText(name, null, true, true);
                checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                checkBoxCell.setOnClickListener(new PrivacySettingsActivity$$Lambda$9(this));
            }
            BottomSheetCell cell2 = new BottomSheetCell(getParentActivity(), 1);
            cell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            cell2.setTextAndIcon(LocaleController.getString("ClearButton", CLASSNAMER.string.ClearButton).toUpperCase(), 0);
            cell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            cell2.setOnClickListener(new PrivacySettingsActivity$$Lambda$10(this));
            linearLayout.addView(cell2, LayoutHelper.createLinear(-1, 50));
            builder2.setCustomView(linearLayout);
            showDialog(builder2.create());
        } else if (position == this.passportRow) {
            presentFragment(new PassportActivity(5, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, null, null, null, null, null));
        }
    }

    final /* synthetic */ void lambda$null$3$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_clearAllDrafts(), new PrivacySettingsActivity$$Lambda$18(this));
    }

    final /* synthetic */ void lambda$null$1$PrivacySettingsActivity() {
        DataQuery.getInstance(this.currentAccount).clearAllDrafts();
    }

    final /* synthetic */ void lambda$null$2$PrivacySettingsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$Lambda$19(this));
    }

    final /* synthetic */ void lambda$null$6$PrivacySettingsActivity(DialogInterface dialog, int which) {
        int value = 0;
        if (which == 0) {
            value = 30;
        } else if (which == 1) {
            value = 90;
        } else if (which == 2) {
            value = 182;
        } else if (which == 3) {
            value = 365;
        }
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCacnel(false);
        progressDialog.show();
        TL_account_setAccountTTL req = new TL_account_setAccountTTL();
        req.ttl = new TL_accountDaysTTL();
        req.ttl.days = value;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PrivacySettingsActivity$$Lambda$16(this, progressDialog, req));
    }

    final /* synthetic */ void lambda$null$5$PrivacySettingsActivity(AlertDialog progressDialog, TL_account_setAccountTTL req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$Lambda$17(this, progressDialog, response, req));
    }

    final /* synthetic */ void lambda$null$4$PrivacySettingsActivity(AlertDialog progressDialog, TLObject response, TL_account_setAccountTTL req) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (response instanceof TL_boolTrue) {
            ContactsController.getInstance(this.currentAccount).setDeleteAccountTTL(req.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    final /* synthetic */ void lambda$null$8$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        this.progressDialog = new Builder(getParentActivity(), 3).show();
        this.progressDialog.setCanCacnel(false);
        if (this.currentSync != this.newSync) {
            UserConfig instance = UserConfig.getInstance(this.currentAccount);
            boolean z = this.newSync;
            instance.syncContacts = z;
            this.currentSync = z;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        ContactsController.getInstance(this.currentAccount).deleteAllContacts(new PrivacySettingsActivity$$Lambda$15(this));
    }

    final /* synthetic */ void lambda$null$7$PrivacySettingsActivity() {
        this.progressDialog.dismiss();
    }

    final /* synthetic */ void lambda$null$11$PrivacySettingsActivity(TextCheckCell cell, DialogInterface dialogInterface, int i) {
        TL_payments_clearSavedInfo req = new TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PrivacySettingsActivity$$Lambda$13(this, cell));
    }

    final /* synthetic */ void lambda$null$10$PrivacySettingsActivity(TextCheckCell cell, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$Lambda$14(this, cell));
    }

    final /* synthetic */ void lambda$null$9$PrivacySettingsActivity(TextCheckCell cell) {
        this.newSuggest = !this.newSuggest;
        cell.setChecked(this.newSuggest);
    }

    final /* synthetic */ void lambda$null$12$PrivacySettingsActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    final /* synthetic */ void lambda$null$13$PrivacySettingsActivity(View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        this.clear[num] = !this.clear[num];
        cell.setChecked(this.clear[num], true);
    }

    final /* synthetic */ void lambda$null$16$PrivacySettingsActivity(View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        Builder builder1 = new Builder(getParentActivity());
        builder1.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
        builder1.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", CLASSNAMER.string.PrivacyPaymentsClearAlert));
        builder1.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new PrivacySettingsActivity$$Lambda$11(this));
        builder1.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
        showDialog(builder1.create());
    }

    final /* synthetic */ void lambda$null$15$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        TL_payments_clearSavedInfo req = new TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, PrivacySettingsActivity$$Lambda$12.$instance);
    }

    static final /* synthetic */ void lambda$null$14$PrivacySettingsActivity(TLObject response, TL_error error) {
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated && this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.privacySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.blockedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lastSeenRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.p2pRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.securitySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.passcodeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.passwordRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sessionsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.advancedSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.clearDraftsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.botsSectionRow = i;
        if (UserConfig.getInstance(this.currentAccount).hasSecureData) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.passportRow = i;
        } else {
            this.passportRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.paymentsClearRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.webSessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.botsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsDeleteRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsSyncRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsSuggestRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.secretSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.secretMapRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.secretWebpageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.secretDetailRow = i;
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        if (!UserConfig.getInstance(this.currentAccount).hasSecureData) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PrivacySettingsActivity$$Lambda$2(this), 10);
        }
    }

    final /* synthetic */ void lambda$loadPasswordSettings$19$PrivacySettingsActivity(TLObject response, TL_error error) {
        if (response != null && ((TL_account_password) response).has_secure_values) {
            AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$Lambda$3(this));
        }
    }

    final /* synthetic */ void lambda$null$18$PrivacySettingsActivity() {
        UserConfig.getInstance(this.currentAccount).hasSecureData = true;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        updateRows();
    }

    private String formatRulesString(int rulesType) {
        ArrayList<PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(rulesType);
        if (privacyRules.size() != 0) {
            int type = -1;
            int plus = 0;
            int minus = 0;
            for (int a = 0; a < privacyRules.size(); a++) {
                PrivacyRule rule = (PrivacyRule) privacyRules.get(a);
                if (rule instanceof TL_privacyValueAllowUsers) {
                    plus += rule.users.size();
                } else if (rule instanceof TL_privacyValueDisallowUsers) {
                    minus += rule.users.size();
                } else if (rule instanceof TL_privacyValueAllowAll) {
                    type = 0;
                } else if (rule instanceof TL_privacyValueDisallowAll) {
                    type = 1;
                } else {
                    type = 2;
                }
            }
            if (type == 0 || (type == -1 && minus > 0)) {
                if (rulesType == 3) {
                    if (minus == 0) {
                        return LocaleController.getString("P2PEverybody", CLASSNAMER.string.P2PEverybody);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", CLASSNAMER.string.P2PEverybodyMinus, Integer.valueOf(minus));
                } else if (minus == 0) {
                    return LocaleController.getString("LastSeenEverybody", CLASSNAMER.string.LastSeenEverybody);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", CLASSNAMER.string.LastSeenEverybodyMinus, Integer.valueOf(minus));
                }
            } else if (type == 2 || (type == -1 && minus > 0 && plus > 0)) {
                if (rulesType == 3) {
                    if (plus == 0 && minus == 0) {
                        return LocaleController.getString("P2PContacts", CLASSNAMER.string.P2PContacts);
                    }
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("P2PContactsMinusPlus", CLASSNAMER.string.P2PContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("P2PContactsMinus", CLASSNAMER.string.P2PContactsMinus, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("P2PContactsPlus", CLASSNAMER.string.P2PContactsPlus, Integer.valueOf(plus));
                    }
                } else if (plus == 0 && minus == 0) {
                    return LocaleController.getString("LastSeenContacts", CLASSNAMER.string.LastSeenContacts);
                } else {
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinusPlus", CLASSNAMER.string.LastSeenContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinus", CLASSNAMER.string.LastSeenContactsMinus, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("LastSeenContactsPlus", CLASSNAMER.string.LastSeenContactsPlus, Integer.valueOf(plus));
                    }
                }
            } else if (type != 1 && plus <= 0) {
                return "unknown";
            } else {
                if (rulesType == 3) {
                    if (plus == 0) {
                        return LocaleController.getString("P2PNobody", CLASSNAMER.string.P2PNobody);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", CLASSNAMER.string.P2PNobodyPlus, Integer.valueOf(plus));
                } else if (plus == 0) {
                    return LocaleController.getString("LastSeenNobody", CLASSNAMER.string.LastSeenNobody);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", CLASSNAMER.string.LastSeenNobodyPlus, Integer.valueOf(plus));
                }
            }
        } else if (rulesType == 3) {
            return LocaleController.getString("P2PNobody", CLASSNAMER.string.P2PNobody);
        } else {
            return LocaleController.getString("LastSeenNobody", CLASSNAMER.string.LastSeenNobody);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[18];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        return r9;
    }
}
