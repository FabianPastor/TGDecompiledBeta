package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
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
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    private int forwardsRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean newSuggest;
    private boolean newSync;
    private int passcodeRow;
    private int passportRow;
    private int passwordRow;
    private int paymentsClearRow;
    private int phoneNumberRow;
    private int privacySectionRow;
    private int profilePhotoRow;
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == PrivacySettingsActivity.this.passcodeRow || adapterPosition == PrivacySettingsActivity.this.passwordRow || adapterPosition == PrivacySettingsActivity.this.blockedRow || adapterPosition == PrivacySettingsActivity.this.sessionsRow || adapterPosition == PrivacySettingsActivity.this.secretWebpageRow || adapterPosition == PrivacySettingsActivity.this.webSessionsRow || adapterPosition == PrivacySettingsActivity.this.clearDraftsRow || ((adapterPosition == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) || ((adapterPosition == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) || ((adapterPosition == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) || ((adapterPosition == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) || ((adapterPosition == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) || ((adapterPosition == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) || ((adapterPosition == PrivacySettingsActivity.this.deleteAccountRow && !PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) || adapterPosition == PrivacySettingsActivity.this.paymentsClearRow || adapterPosition == PrivacySettingsActivity.this.secretMapRow || adapterPosition == PrivacySettingsActivity.this.contactsSyncRow || adapterPosition == PrivacySettingsActivity.this.passportRow || adapterPosition == PrivacySettingsActivity.this.contactsDeleteRow || adapterPosition == PrivacySettingsActivity.this.contactsSuggestRow)))))))) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                textSettingsCell = new TextInfoPrivacyCell(this.mContext);
            } else if (i != 2) {
                textSettingsCell = new TextCheckCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(str));
            } else {
                textSettingsCell = new HeaderCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(textSettingsCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            String str;
            String string;
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.blockedRow) {
                    i = PrivacySettingsActivity.this.getMessagesController().totalBlockedCount;
                    str = "BlockedUsers";
                    if (i == 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("BlockedEmpty", NUM), true);
                    } else if (i > 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), String.format("%d", new Object[]{Integer.valueOf(i)}), true);
                    } else {
                        textSettingsCell.setText(LocaleController.getString(str, NUM), true);
                    }
                } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("SessionsTitle", NUM), false);
                } else if (i == PrivacySettingsActivity.this.webSessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("WebSessionsTitle", NUM), false);
                } else if (i == PrivacySettingsActivity.this.passwordRow) {
                    textSettingsCell.setText(LocaleController.getString("TwoStepVerification", NUM), true);
                } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                    textSettingsCell.setText(LocaleController.getString("Passcode", NUM), true);
                } else {
                    String str2 = "Loading";
                    if (i == PrivacySettingsActivity.this.phoneNumberRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyPhone", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.groupsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", NUM), string, false);
                    } else if (i == PrivacySettingsActivity.this.callsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Calls", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.profilePhotoRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.forwardsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            string = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyForwards", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.passportRow) {
                        textSettingsCell.setText(LocaleController.getString("TelegramPassport", NUM), true);
                    } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                            string = LocaleController.getString(str2, NUM);
                        } else {
                            i = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                            if (i <= 182) {
                                string = LocaleController.formatPluralString("Months", i / 30);
                            } else if (i == 365) {
                                string = LocaleController.formatPluralString("Years", i / 365);
                            } else {
                                string = LocaleController.formatPluralString("Days", i);
                            }
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor2", NUM), string, false);
                    } else if (i == PrivacySettingsActivity.this.clearDraftsRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), true);
                    } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyPaymentsClear", NUM), true);
                    } else if (i == PrivacySettingsActivity.this.secretMapRow) {
                        i = SharedConfig.mapPreviewType;
                        if (i == 0) {
                            string = LocaleController.getString("MapPreviewProviderTelegram", NUM);
                        } else if (i == 1) {
                            string = LocaleController.getString("MapPreviewProviderGoogle", NUM);
                        } else if (i != 2) {
                            string = LocaleController.getString("MapPreviewProviderYandex", NUM);
                        } else {
                            string = LocaleController.getString("MapPreviewProviderNobody", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", NUM), string, true);
                    } else if (i == PrivacySettingsActivity.this.contactsDeleteRow) {
                        textSettingsCell.setText(LocaleController.getString("SyncContactsDelete", NUM), true);
                    }
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                str = "windowBackgroundGrayShadow";
                if (i == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("DeleteAccountHelp", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SessionsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PrivacySettingsActivity.this.secretDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SecretWebPageInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PrivacySettingsActivity.this.botsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("PrivacyBotsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PrivacySettingsActivity.this.contactsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SuggestContactsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.privacySectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyTitle", NUM));
                } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                    headerCell.setText(LocaleController.getString("SecurityTitle", NUM));
                } else if (i == PrivacySettingsActivity.this.advancedSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyAdvanced", NUM));
                } else if (i == PrivacySettingsActivity.this.secretSectionRow) {
                    headerCell.setText(LocaleController.getString("SecretChat", NUM));
                } else if (i == PrivacySettingsActivity.this.botsSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyBots", NUM));
                } else if (i == PrivacySettingsActivity.this.contactsSectionRow) {
                    headerCell.setText(LocaleController.getString("Contacts", NUM));
                }
            } else if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                    string = LocaleController.getString("SecretWebPage", NUM);
                    if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                        z = false;
                    }
                    textCheckCell.setTextAndCheck(string, z, false);
                } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", NUM), PrivacySettingsActivity.this.newSync, true);
                } else if (i == PrivacySettingsActivity.this.contactsSuggestRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", NUM), PrivacySettingsActivity.this.newSuggest, false);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == PrivacySettingsActivity.this.passportRow || i == PrivacySettingsActivity.this.lastSeenRow || i == PrivacySettingsActivity.this.phoneNumberRow || i == PrivacySettingsActivity.this.blockedRow || i == PrivacySettingsActivity.this.deleteAccountRow || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.webSessionsRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.groupsRow || i == PrivacySettingsActivity.this.paymentsClearRow || i == PrivacySettingsActivity.this.secretMapRow || i == PrivacySettingsActivity.this.contactsDeleteRow || i == PrivacySettingsActivity.this.clearDraftsRow) {
                return 0;
            }
            if (i == PrivacySettingsActivity.this.deleteAccountDetailRow || i == PrivacySettingsActivity.this.groupsDetailRow || i == PrivacySettingsActivity.this.sessionsDetailRow || i == PrivacySettingsActivity.this.secretDetailRow || i == PrivacySettingsActivity.this.botsDetailRow || i == PrivacySettingsActivity.this.contactsDetailRow) {
                return 1;
            }
            if (i == PrivacySettingsActivity.this.securitySectionRow || i == PrivacySettingsActivity.this.advancedSectionRow || i == PrivacySettingsActivity.this.privacySectionRow || i == PrivacySettingsActivity.this.secretSectionRow || i == PrivacySettingsActivity.this.botsSectionRow || i == PrivacySettingsActivity.this.contactsSectionRow) {
                return 2;
            }
            if (i == PrivacySettingsActivity.this.secretWebpageRow || i == PrivacySettingsActivity.this.contactsSyncRow || i == PrivacySettingsActivity.this.contactsSuggestRow) {
                return 3;
            }
            return 0;
        }
    }

    static /* synthetic */ void lambda$null$14(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject tLObject, TL_error tL_error) {
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getContactsController().loadPrivacySettings();
        getMessagesController().getBlockedUsers(true);
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        z = getUserConfig().suggestContacts;
        this.newSuggest = z;
        this.currentSuggest = z;
        updateRows();
        loadPasswordSettings();
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        if (this.currentSync != this.newSync) {
            getUserConfig().syncContacts = this.newSync;
            getUserConfig().saveConfig(false);
            if (this.newSync) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", NUM), 0).show();
                }
            }
        }
        boolean z = this.newSuggest;
        if (z != this.currentSuggest) {
            if (!z) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            getUserConfig().saveConfig(false);
            TL_contacts_toggleTopPeers tL_contacts_toggleTopPeers = new TL_contacts_toggleTopPeers();
            tL_contacts_toggleTopPeers.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(tL_contacts_toggleTopPeers, -$$Lambda$PrivacySettingsActivity$IBy4A3n5R5n7oI7wDCjl___b3rk.INSTANCE);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivacySettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass2 anonymousClass2 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass2;
        recyclerListView.setLayoutManager(anonymousClass2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$PrivacySettingsActivity$4NW4D3yhnbANs4O1uZu09FeFwHI(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$17$PrivacySettingsActivity(View view, int i) {
        View view2 = view;
        int i2 = i;
        if (view.isEnabled()) {
            if (i2 == this.blockedRow) {
                presentFragment(new PrivacyUsersActivity());
            } else {
                boolean z = false;
                if (i2 == this.sessionsRow) {
                    presentFragment(new SessionsActivity(0));
                } else if (i2 == this.webSessionsRow) {
                    presentFragment(new SessionsActivity(1));
                } else {
                    String str = "Delete";
                    String str2 = "dialogTextRed2";
                    String str3 = "Cancel";
                    Builder builder;
                    AlertDialog create;
                    TextView textView;
                    Builder builder2;
                    TextCheckCell textCheckCell;
                    if (i2 == this.clearDraftsRow) {
                        builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AreYouSureClearDraftsTitle", NUM));
                        builder.setMessage(LocaleController.getString("AreYouSureClearDrafts", NUM));
                        builder.setPositiveButton(LocaleController.getString(str, NUM), new -$$Lambda$PrivacySettingsActivity$ylpEMcd3Q-fRxhVvfyDGjrZOk0Q(this));
                        builder.setNegativeButton(LocaleController.getString(str3, NUM), null);
                        create = builder.create();
                        showDialog(create);
                        textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor(str2));
                        }
                    } else if (i2 == this.deleteAccountRow) {
                        if (getParentActivity() != null) {
                            int deleteAccountTTL = getContactsController().getDeleteAccountTTL();
                            deleteAccountTTL = deleteAccountTTL <= 31 ? 0 : deleteAccountTTL <= 93 ? 1 : deleteAccountTTL <= 182 ? 2 : 3;
                            builder2 = new Builder(getParentActivity());
                            builder2.setTitle(LocaleController.getString("DeleteAccountTitle", NUM));
                            r6 = new String[4];
                            str = "Months";
                            r6[0] = LocaleController.formatPluralString(str, 1);
                            r6[1] = LocaleController.formatPluralString(str, 3);
                            r6[2] = LocaleController.formatPluralString(str, 6);
                            r6[3] = LocaleController.formatPluralString("Years", 1);
                            LinearLayout linearLayout = new LinearLayout(getParentActivity());
                            linearLayout.setOrientation(1);
                            builder2.setView(linearLayout);
                            int i3 = 0;
                            while (i3 < r6.length) {
                                RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                                radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                radioColorCell.setTag(Integer.valueOf(i3));
                                radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                                radioColorCell.setTextAndValue(r6[i3], deleteAccountTTL == i3);
                                linearLayout.addView(radioColorCell);
                                radioColorCell.setOnClickListener(new -$$Lambda$PrivacySettingsActivity$EbD-HCWXty56Ft6CXhbxfq3BzZE(this, builder2));
                                i3++;
                            }
                            builder2.setNegativeButton(LocaleController.getString(str3, NUM), null);
                            showDialog(builder2.create());
                        }
                    } else if (i2 == this.lastSeenRow) {
                        presentFragment(new PrivacyControlActivity(0));
                    } else if (i2 == this.phoneNumberRow) {
                        presentFragment(new PrivacyControlActivity(6));
                    } else if (i2 == this.groupsRow) {
                        presentFragment(new PrivacyControlActivity(1));
                    } else if (i2 == this.callsRow) {
                        presentFragment(new PrivacyControlActivity(2));
                    } else if (i2 == this.profilePhotoRow) {
                        presentFragment(new PrivacyControlActivity(4));
                    } else if (i2 == this.forwardsRow) {
                        presentFragment(new PrivacyControlActivity(5));
                    } else if (i2 == this.passwordRow) {
                        presentFragment(new TwoStepVerificationActivity(0));
                    } else if (i2 == this.passcodeRow) {
                        if (SharedConfig.passcodeHash.length() > 0) {
                            presentFragment(new PasscodeActivity(2));
                        } else {
                            presentFragment(new PasscodeActivity(0));
                        }
                    } else if (i2 == this.secretWebpageRow) {
                        if (getMessagesController().secretWebpagePreview == 1) {
                            getMessagesController().secretWebpagePreview = 0;
                        } else {
                            getMessagesController().secretWebpagePreview = 1;
                        }
                        MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", getMessagesController().secretWebpagePreview).commit();
                        if (view2 instanceof TextCheckCell) {
                            textCheckCell = (TextCheckCell) view2;
                            if (getMessagesController().secretWebpagePreview == 1) {
                                z = true;
                            }
                            textCheckCell.setChecked(z);
                        }
                    } else if (i2 == this.contactsDeleteRow) {
                        if (getParentActivity() != null) {
                            builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("SyncContactsDeleteTitle", NUM));
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", NUM)));
                            builder.setNegativeButton(LocaleController.getString(str3, NUM), null);
                            builder.setPositiveButton(LocaleController.getString(str, NUM), new -$$Lambda$PrivacySettingsActivity$XTkqLHKOC0QMU4JQevFxzR8pfWg(this));
                            create = builder.create();
                            showDialog(create);
                            textView = (TextView) create.getButton(-1);
                            if (textView != null) {
                                textView.setTextColor(Theme.getColor(str2));
                            }
                        }
                    } else if (i2 == this.contactsSuggestRow) {
                        textCheckCell = (TextCheckCell) view2;
                        boolean z2 = this.newSuggest;
                        if (z2) {
                            builder2 = new Builder(getParentActivity());
                            builder2.setTitle(LocaleController.getString("SuggestContactsTitle", NUM));
                            builder2.setMessage(LocaleController.getString("SuggestContactsAlert", NUM));
                            builder2.setPositiveButton(LocaleController.getString("MuteDisable", NUM), new -$$Lambda$PrivacySettingsActivity$5uWMtQO-U72VaNWgF2qczAQ3ehw(this, textCheckCell));
                            builder2.setNegativeButton(LocaleController.getString(str3, NUM), null);
                            create = builder2.create();
                            showDialog(create);
                            textView = (TextView) create.getButton(-1);
                            if (textView != null) {
                                textView.setTextColor(Theme.getColor(str2));
                            }
                        } else {
                            this.newSuggest = z2 ^ 1;
                            textCheckCell.setChecked(this.newSuggest);
                        }
                    } else if (i2 == this.contactsSyncRow) {
                        this.newSync ^= 1;
                        if (view2 instanceof TextCheckCell) {
                            ((TextCheckCell) view2).setChecked(this.newSync);
                        }
                    } else if (i2 == this.secretMapRow) {
                        AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new -$$Lambda$PrivacySettingsActivity$UDnLY7DIuRnfm37P1EPe2aUKeCM(this), false);
                    } else if (i2 == this.paymentsClearRow) {
                        builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
                        builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", NUM));
                        LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                        linearLayout2.setOrientation(1);
                        builder.setView(linearLayout2);
                        int i4 = 0;
                        while (i4 < 2) {
                            CharSequence string = i4 == 0 ? LocaleController.getString("PrivacyClearShipping", NUM) : i4 == 1 ? LocaleController.getString("PrivacyClearPayment", NUM) : null;
                            this.clear[i4] = true;
                            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                            checkBoxCell.setTag(Integer.valueOf(i4));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            linearLayout2.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                            checkBoxCell.setText(string, null, true, false);
                            checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                            checkBoxCell.setOnClickListener(new -$$Lambda$PrivacySettingsActivity$Z17JwQmal4wl3EV9bgEY12pZChE(this));
                            i4++;
                        }
                        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM), new -$$Lambda$PrivacySettingsActivity$EToHlQNWubo1BhEUmV1WaUYkJ2Q(this));
                        builder.setNegativeButton(LocaleController.getString(str3, NUM), null);
                        showDialog(builder.create());
                        create = builder.create();
                        showDialog(create);
                        textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor(str2));
                        }
                    } else if (i2 == this.passportRow) {
                        presentFragment(new PassportActivity(5, 0, "", "", null, null, null, null, null));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$3$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TL_messages_clearAllDrafts(), new -$$Lambda$PrivacySettingsActivity$s-IxNGqvJf4sWGFlV1kwOiRILw0(this));
    }

    public /* synthetic */ void lambda$null$1$PrivacySettingsActivity() {
        getMediaDataController().clearAllDrafts(true);
    }

    public /* synthetic */ void lambda$null$2$PrivacySettingsActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacySettingsActivity$f1z5m_pXWI9fMMWGfuS4tAirj68(this));
    }

    public /* synthetic */ void lambda$null$6$PrivacySettingsActivity(Builder builder, View view) {
        builder.getDismissRunnable().run();
        Integer num = (Integer) view.getTag();
        int i = num.intValue() == 0 ? 30 : num.intValue() == 1 ? 90 : num.intValue() == 2 ? 182 : num.intValue() == 3 ? 365 : 0;
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        TL_account_setAccountTTL tL_account_setAccountTTL = new TL_account_setAccountTTL();
        tL_account_setAccountTTL.ttl = new TL_accountDaysTTL();
        tL_account_setAccountTTL.ttl.days = i;
        getConnectionsManager().sendRequest(tL_account_setAccountTTL, new -$$Lambda$PrivacySettingsActivity$J8W4oBcdXleAkqcvNpMiNXEJRwc(this, alertDialog, tL_account_setAccountTTL));
    }

    public /* synthetic */ void lambda$null$5$PrivacySettingsActivity(AlertDialog alertDialog, TL_account_setAccountTTL tL_account_setAccountTTL, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacySettingsActivity$LsF3KI7j3KNxTTiwgalmqj7HqKo(this, alertDialog, tLObject, tL_account_setAccountTTL));
    }

    public /* synthetic */ void lambda$null$4$PrivacySettingsActivity(AlertDialog alertDialog, TLObject tLObject, TL_account_setAccountTTL tL_account_setAccountTTL) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_boolTrue) {
            getContactsController().setDeleteAccountTTL(tL_account_setAccountTTL.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$null$8$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        this.progressDialog = new Builder(getParentActivity(), 3).show();
        this.progressDialog.setCanCacnel(false);
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z = this.newSync;
            userConfig.syncContacts = z;
            this.currentSync = z;
            getUserConfig().saveConfig(false);
        }
        getContactsController().deleteAllContacts(new -$$Lambda$PrivacySettingsActivity$AceH1btqmuD9dCVsGDT9w8V0zXo(this));
    }

    public /* synthetic */ void lambda$null$7$PrivacySettingsActivity() {
        this.progressDialog.dismiss();
    }

    public /* synthetic */ void lambda$null$11$PrivacySettingsActivity(TextCheckCell textCheckCell, DialogInterface dialogInterface, int i) {
        TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tL_payments_clearSavedInfo.credentials = zArr[1];
        tL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tL_payments_clearSavedInfo, new -$$Lambda$PrivacySettingsActivity$9W-WSazbZdTZMbEaunliHg9yKU0(this, textCheckCell));
    }

    public /* synthetic */ void lambda$null$10$PrivacySettingsActivity(TextCheckCell textCheckCell, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacySettingsActivity$DlGV63A9ypZdVnkR6Qd7Km-hz-I(this, textCheckCell));
    }

    public /* synthetic */ void lambda$null$9$PrivacySettingsActivity(TextCheckCell textCheckCell) {
        this.newSuggest ^= 1;
        textCheckCell.setChecked(this.newSuggest);
    }

    public /* synthetic */ void lambda$null$12$PrivacySettingsActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$13$PrivacySettingsActivity(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[intValue] = zArr[intValue] ^ 1;
        checkBoxCell.setChecked(zArr[intValue], true);
    }

    public /* synthetic */ void lambda$null$16$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
        builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM), new -$$Lambda$PrivacySettingsActivity$deqj30NjdD0kcQ7DKtdHwkANODM(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    public /* synthetic */ void lambda$null$15$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tL_payments_clearSavedInfo.credentials = zArr[1];
        tL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tL_payments_clearSavedInfo, -$$Lambda$PrivacySettingsActivity$RbXVegSlA75NxctCB1pTrsCmwsU.INSTANCE);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            this.listAdapter.notifyItemChanged(this.blockedRow);
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
        this.phoneNumberRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lastSeenRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profilePhotoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.forwardsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsRow = i;
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
        if (getUserConfig().hasSecureData) {
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
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        if (!getUserConfig().hasSecureData) {
            getConnectionsManager().sendRequest(new TL_account_getPassword(), new -$$Lambda$PrivacySettingsActivity$7MIQgKJpVA2M6eCuYxDI1zmVMaY(this), 10);
        }
    }

    public /* synthetic */ void lambda$loadPasswordSettings$19$PrivacySettingsActivity(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null && ((TL_account_password) tLObject).has_secure_values) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PrivacySettingsActivity$IncnhooBIVbvbnrTjiT1GT2RRI0(this));
        }
    }

    public /* synthetic */ void lambda$null$18$PrivacySettingsActivity() {
        getUserConfig().hasSecureData = true;
        getUserConfig().saveConfig(false);
        updateRows();
    }

    public static String formatRulesString(AccountInstance accountInstance, int i) {
        int i2 = i;
        ArrayList privacyRules = accountInstance.getContactsController().getPrivacyRules(i2);
        String str = "P2PNobody";
        String str2 = "LastSeenNobody";
        if (privacyRules.size() != 0) {
            Object obj = -1;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < privacyRules.size(); i5++) {
                PrivacyRule privacyRule = (PrivacyRule) privacyRules.get(i5);
                int size;
                if (privacyRule instanceof TL_privacyValueAllowChatParticipants) {
                    TL_privacyValueAllowChatParticipants tL_privacyValueAllowChatParticipants = (TL_privacyValueAllowChatParticipants) privacyRule;
                    size = tL_privacyValueAllowChatParticipants.chats.size();
                    int i6 = i4;
                    for (i4 = 0; i4 < size; i4++) {
                        Chat chat = accountInstance.getMessagesController().getChat((Integer) tL_privacyValueAllowChatParticipants.chats.get(i4));
                        if (chat != null) {
                            i6 += chat.participants_count;
                        }
                    }
                    i4 = i6;
                } else if (privacyRule instanceof TL_privacyValueDisallowChatParticipants) {
                    TL_privacyValueDisallowChatParticipants tL_privacyValueDisallowChatParticipants = (TL_privacyValueDisallowChatParticipants) privacyRule;
                    size = tL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i7 = 0; i7 < size; i7++) {
                        Chat chat2 = accountInstance.getMessagesController().getChat((Integer) tL_privacyValueDisallowChatParticipants.chats.get(i7));
                        if (chat2 != null) {
                            i3 += chat2.participants_count;
                        }
                    }
                } else if (privacyRule instanceof TL_privacyValueAllowUsers) {
                    i4 += ((TL_privacyValueAllowUsers) privacyRule).users.size();
                } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                    i3 += ((TL_privacyValueDisallowUsers) privacyRule).users.size();
                } else if (obj == -1) {
                    obj = privacyRule instanceof TL_privacyValueAllowAll ? null : privacyRule instanceof TL_privacyValueDisallowAll ? 1 : 2;
                }
            }
            if (obj == null || (obj == -1 && i3 > 0)) {
                if (i2 == 3) {
                    if (i3 == 0) {
                        return LocaleController.getString("P2PEverybody", NUM);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", NUM, Integer.valueOf(i3));
                } else if (i3 == 0) {
                    return LocaleController.getString("LastSeenEverybody", NUM);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", NUM, Integer.valueOf(i3));
                }
            } else if (obj == 2 || (obj == -1 && i3 > 0 && i4 > 0)) {
                if (i2 == 3) {
                    if (i4 == 0 && i3 == 0) {
                        return LocaleController.getString("P2PContacts", NUM);
                    }
                    if (i4 != 0 && i3 != 0) {
                        return LocaleController.formatString("P2PContactsMinusPlus", NUM, Integer.valueOf(i3), Integer.valueOf(i4));
                    } else if (i3 != 0) {
                        return LocaleController.formatString("P2PContactsMinus", NUM, Integer.valueOf(i3));
                    } else {
                        return LocaleController.formatString("P2PContactsPlus", NUM, Integer.valueOf(i4));
                    }
                } else if (i4 == 0 && i3 == 0) {
                    return LocaleController.getString("LastSeenContacts", NUM);
                } else {
                    if (i4 != 0 && i3 != 0) {
                        return LocaleController.formatString("LastSeenContactsMinusPlus", NUM, Integer.valueOf(i3), Integer.valueOf(i4));
                    } else if (i3 != 0) {
                        return LocaleController.formatString("LastSeenContactsMinus", NUM, Integer.valueOf(i3));
                    } else {
                        return LocaleController.formatString("LastSeenContactsPlus", NUM, Integer.valueOf(i4));
                    }
                }
            } else if (obj != 1 && i4 <= 0) {
                return "unknown";
            } else {
                if (i2 == 3) {
                    if (i4 == 0) {
                        return LocaleController.getString(str, NUM);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", NUM, Integer.valueOf(i4));
                } else if (i4 == 0) {
                    return LocaleController.getString(str2, NUM);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", NUM, Integer.valueOf(i4));
                }
            }
        } else if (i2 == 3) {
            return LocaleController.getString(str, NUM);
        } else {
            return LocaleController.getString(str2, NUM);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[18];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        return themeDescriptionArr;
    }
}
