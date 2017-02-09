package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int blockedRow;
    private int callsRow;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private int deleteAccountSectionRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int passcodeRow;
    private int passwordRow;
    private int privacySectionRow;
    private int rowCount;
    private int secretDetailRow;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.secretWebpageRow || ((position == PrivacySettingsActivity.this.groupsRow && !ContactsController.getInstance().getLoadingGroupInfo()) || ((position == PrivacySettingsActivity.this.lastSeenRow && !ContactsController.getInstance().getLoadingLastSeenInfo()) || ((position == PrivacySettingsActivity.this.callsRow && !ContactsController.getInstance().getLoadingCallsInfo()) || (position == PrivacySettingsActivity.this.deleteAccountRow && !ContactsController.getInstance().getLoadingDeleteInfo()))));
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
            int i = R.drawable.greydivider_bottom;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.blockedRow) {
                        textCell.setText(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.sessionsRow) {
                        textCell.setText(LocaleController.getString("SessionsTitle", R.string.SessionsTitle), false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.passwordRow) {
                        textCell.setText(LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.passcodeRow) {
                        textCell.setText(LocaleController.getString("Passcode", R.string.Passcode), true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.lastSeenRow) {
                        if (ContactsController.getInstance().getLoadingLastSeenInfo()) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(0);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.callsRow) {
                        if (ContactsController.getInstance().getLoadingCallsInfo()) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(2);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Calls", R.string.Calls), value, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.groupsRow) {
                        if (ContactsController.getInstance().getLoadingGroupInfo()) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.this.formatRulesString(1);
                        }
                        textCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), value, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (ContactsController.getInstance().getLoadingDeleteInfo()) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else {
                            int ttl = ContactsController.getInstance().getDeleteAccountTTL();
                            if (ttl <= 182) {
                                value = LocaleController.formatPluralString("Months", ttl / 30);
                            } else if (ttl == 365) {
                                value = LocaleController.formatPluralString("Years", ttl / 365);
                            } else {
                                value = LocaleController.formatPluralString("Days", ttl);
                            }
                        }
                        textCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor", R.string.DeleteAccountIfAwayFor), value, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                        privacyCell.setText(LocaleController.getString("DeleteAccountHelp", R.string.DeleteAccountHelp));
                        Context context = this.mContext;
                        if (PrivacySettingsActivity.this.secretSectionRow != -1) {
                            i = R.drawable.greydivider;
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.groupsDetailRow) {
                        privacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", R.string.GroupsAndChannelsHelp));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.sessionsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SessionsInfo", R.string.SessionsInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretDetailRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.privacySectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                        return;
                    } else if (position == PrivacySettingsActivity.this.securitySectionRow) {
                        headerCell.setText(LocaleController.getString("SecurityTitle", R.string.SecurityTitle));
                        return;
                    } else if (position == PrivacySettingsActivity.this.deleteAccountSectionRow) {
                        headerCell.setText(LocaleController.getString("DeleteAccountTitle", R.string.DeleteAccountTitle));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretSectionRow) {
                        headerCell.setText(LocaleController.getString("SecretChat", R.string.SecretChat));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCheckCell = holder.itemView;
                    if (position == PrivacySettingsActivity.this.secretWebpageRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SecretWebPage", R.string.SecretWebPage), MessagesController.getInstance().secretWebpagePreview == 1, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacySettingsActivity.this.lastSeenRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.deleteAccountRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.groupsRow) {
                return 0;
            }
            if (position == PrivacySettingsActivity.this.deleteAccountDetailRow || position == PrivacySettingsActivity.this.groupsDetailRow || position == PrivacySettingsActivity.this.sessionsDetailRow || position == PrivacySettingsActivity.this.secretDetailRow) {
                return 1;
            }
            if (position == PrivacySettingsActivity.this.securitySectionRow || position == PrivacySettingsActivity.this.deleteAccountSectionRow || position == PrivacySettingsActivity.this.privacySectionRow || position == PrivacySettingsActivity.this.secretSectionRow) {
                return 2;
            }
            if (position == PrivacySettingsActivity.this.secretWebpageRow) {
                return 3;
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ContactsController.getInstance().loadPrivacySettings();
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
        if (MessagesController.getInstance().callsEnabled) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsRow = i;
        } else {
            this.callsRow = -1;
        }
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
        this.deleteAccountSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteAccountDetailRow = i;
        if (MessagesController.getInstance().secretWebpagePreview != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretWebpageRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.secretDetailRow = i;
        } else {
            this.secretSectionRow = -1;
            this.secretWebpageRow = -1;
            this.secretDetailRow = -1;
        }
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
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacySettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                boolean z = true;
                if (position == PrivacySettingsActivity.this.blockedRow) {
                    PrivacySettingsActivity.this.presentFragment(new BlockedUsersActivity());
                } else if (position == PrivacySettingsActivity.this.sessionsRow) {
                    PrivacySettingsActivity.this.presentFragment(new SessionsActivity());
                } else if (position == PrivacySettingsActivity.this.deleteAccountRow) {
                    if (PrivacySettingsActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(PrivacySettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("DeleteAccountTitle", R.string.DeleteAccountTitle));
                        builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Months", 1), LocaleController.formatPluralString("Months", 3), LocaleController.formatPluralString("Months", 6), LocaleController.formatPluralString("Years", 1)}, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                final ProgressDialog progressDialog = new ProgressDialog(PrivacySettingsActivity.this.getParentActivity());
                                progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                final TL_account_setAccountTTL req = new TL_account_setAccountTTL();
                                req.ttl = new TL_accountDaysTTL();
                                req.ttl.days = value;
                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                    public void run(final TLObject response, TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    progressDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.e("tmessages", e);
                                                }
                                                if (response instanceof TL_boolTrue) {
                                                    ContactsController.getInstance().setDeleteAccountTTL(req.ttl.days);
                                                    PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        PrivacySettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == PrivacySettingsActivity.this.lastSeenRow) {
                    PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(0));
                } else if (position == PrivacySettingsActivity.this.callsRow) {
                    PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(2));
                } else if (position == PrivacySettingsActivity.this.groupsRow) {
                    PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(1));
                } else if (position == PrivacySettingsActivity.this.passwordRow) {
                    PrivacySettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
                } else if (position == PrivacySettingsActivity.this.passcodeRow) {
                    if (UserConfig.passcodeHash.length() > 0) {
                        PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(2));
                    } else {
                        PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(0));
                    }
                } else if (position == PrivacySettingsActivity.this.secretWebpageRow) {
                    if (MessagesController.getInstance().secretWebpagePreview == 1) {
                        MessagesController.getInstance().secretWebpagePreview = 0;
                    } else {
                        MessagesController.getInstance().secretWebpagePreview = 1;
                    }
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                    if (view instanceof TextCheckCell) {
                        TextCheckCell textCheckCell = (TextCheckCell) view;
                        if (MessagesController.getInstance().secretWebpagePreview != 1) {
                            z = false;
                        }
                        textCheckCell.setChecked(z);
                    }
                }
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated && this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private String formatRulesString(int rulesType) {
        ArrayList<PrivacyRule> privacyRules = ContactsController.getInstance().getPrivacyRules(rulesType);
        if (privacyRules.size() == 0) {
            return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
        }
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
            if (minus == 0) {
                return LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
            }
            return LocaleController.formatString("LastSeenEverybodyMinus", R.string.LastSeenEverybodyMinus, Integer.valueOf(minus));
        } else if (type == 2 || (type == -1 && minus > 0 && plus > 0)) {
            if (plus == 0 && minus == 0) {
                return LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
            }
            if (plus != 0 && minus != 0) {
                return LocaleController.formatString("LastSeenContactsMinusPlus", R.string.LastSeenContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
            } else if (minus != 0) {
                return LocaleController.formatString("LastSeenContactsMinus", R.string.LastSeenContactsMinus, Integer.valueOf(minus));
            } else {
                return LocaleController.formatString("LastSeenContactsPlus", R.string.LastSeenContactsPlus, Integer.valueOf(plus));
            }
        } else if (type != 1 && plus <= 0) {
            return "unknown";
        } else {
            if (plus == 0) {
                return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
            }
            return LocaleController.formatString("LastSeenNobodyPlus", R.string.LastSeenNobodyPlus, Integer.valueOf(plus));
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[21];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelectorSDK21);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        return themeDescriptionArr;
    }
}
