package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
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
import org.telegram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;

public class PrivacySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int blockedRow;
    private int botsDetailRow;
    private int botsSectionRow;
    private int callsDetailRow;
    private int callsP2PRow;
    private int callsRow;
    private int callsSectionRow;
    private boolean[] clear = new boolean[2];
    private int contactsDetailRow;
    private int contactsSectionRow;
    private int contactsSyncRow;
    private boolean currentSync;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private int deleteAccountSectionRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean newSync;
    private int passcodeRow;
    private int passwordRow;
    private int paymentsClearRow;
    private int privacySectionRow;
    private int rowCount;
    private int secretDetailRow;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;
    private int webSessionsRow;

    /* renamed from: org.telegram.ui.PrivacySettingsActivity$1 */
    class C22461 extends ActionBarMenuOnItemClick {
        C22461() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacySettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacySettingsActivity$3 */
    class C22493 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$1 */
        class C16331 implements OnClickListener {
            C16331() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                i = i == 0 ? 30 : i == 1 ? 90 : i == 2 ? 182 : i == 3 ? 365 : 0;
                final AlertDialog alertDialog = new AlertDialog(PrivacySettingsActivity.this.getParentActivity(), 1);
                alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
                dialogInterface = new TL_account_setAccountTTL();
                dialogInterface.ttl = new TL_accountDaysTTL();
                dialogInterface.ttl.days = i;
                ConnectionsManager.getInstance(PrivacySettingsActivity.this.currentAccount).sendRequest(dialogInterface, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    alertDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                if (tLObject instanceof TL_boolTrue) {
                                    ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).setDeleteAccountTTL(dialogInterface.ttl.days);
                                    PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
            }
        }

        /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$2 */
        class C16342 implements OnClickListener {
            C16342() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getMainSettings(PrivacySettingsActivity.this.currentAccount).edit().putInt("calls_p2p_new", i).commit();
                PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$3 */
        class C16353 implements View.OnClickListener {
            C16353() {
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                PrivacySettingsActivity.this.clear[intValue] = PrivacySettingsActivity.this.clear[intValue] ^ true;
                checkBoxCell.setChecked(PrivacySettingsActivity.this.clear[intValue], true);
            }
        }

        /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$4 */
        class C16374 implements View.OnClickListener {

            /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$4$1 */
            class C16361 implements OnClickListener {

                /* renamed from: org.telegram.ui.PrivacySettingsActivity$3$4$1$1 */
                class C22481 implements RequestDelegate {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }

                    C22481() {
                    }
                }

                C16361() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface = new TL_payments_clearSavedInfo();
                    dialogInterface.credentials = PrivacySettingsActivity.this.clear[1];
                    dialogInterface.info = PrivacySettingsActivity.this.clear[0];
                    UserConfig.getInstance(PrivacySettingsActivity.this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(PrivacySettingsActivity.this.currentAccount).saveConfig(false);
                    ConnectionsManager.getInstance(PrivacySettingsActivity.this.currentAccount).sendRequest(dialogInterface, new C22481());
                }
            }

            C16374() {
            }

            public void onClick(View view) {
                try {
                    if (PrivacySettingsActivity.this.visibleDialog != null) {
                        PrivacySettingsActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                view = new Builder(PrivacySettingsActivity.this.getParentActivity());
                view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                view.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", C0446R.string.PrivacyPaymentsClearAlert));
                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16361());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                PrivacySettingsActivity.this.showDialog(view.create());
            }
        }

        C22493() {
        }

        public void onItemClick(View view, int i) {
            if (view.isEnabled()) {
                if (i == PrivacySettingsActivity.this.blockedRow) {
                    PrivacySettingsActivity.this.presentFragment(new BlockedUsersActivity());
                } else {
                    boolean z = false;
                    if (i == PrivacySettingsActivity.this.sessionsRow) {
                        PrivacySettingsActivity.this.presentFragment(new SessionsActivity(0));
                    } else if (i == PrivacySettingsActivity.this.webSessionsRow) {
                        PrivacySettingsActivity.this.presentFragment(new SessionsActivity(1));
                    } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (PrivacySettingsActivity.this.getParentActivity() != null) {
                            view = new Builder(PrivacySettingsActivity.this.getParentActivity());
                            view.setTitle(LocaleController.getString("DeleteAccountTitle", C0446R.string.DeleteAccountTitle));
                            view.setItems(new CharSequence[]{LocaleController.formatPluralString("Months", 1), LocaleController.formatPluralString("Months", 3), LocaleController.formatPluralString("Months", 6), LocaleController.formatPluralString("Years", 1)}, new C16331());
                            view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            PrivacySettingsActivity.this.showDialog(view.create());
                        }
                    } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                        PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(0));
                    } else if (i == PrivacySettingsActivity.this.callsRow) {
                        PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(2));
                    } else if (i == PrivacySettingsActivity.this.groupsRow) {
                        PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(1));
                    } else if (i == PrivacySettingsActivity.this.passwordRow) {
                        PrivacySettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
                    } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                        if (SharedConfig.passcodeHash.length() > null) {
                            PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(2));
                        } else {
                            PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(0));
                        }
                    } else if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                        if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview == 1) {
                            MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview = 0;
                        } else {
                            MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview = 1;
                        }
                        MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview).commit();
                        if ((view instanceof TextCheckCell) != 0) {
                            TextCheckCell textCheckCell = (TextCheckCell) view;
                            if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview == 1) {
                                z = true;
                            }
                            textCheckCell.setChecked(z);
                        }
                    } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                        PrivacySettingsActivity.this.newSync = PrivacySettingsActivity.this.newSync ^ true;
                        if ((view instanceof TextCheckCell) != 0) {
                            ((TextCheckCell) view).setChecked(PrivacySettingsActivity.this.newSync);
                        }
                        PrivacySettingsActivity.this.listAdapter.notifyItemChanged(PrivacySettingsActivity.this.contactsDetailRow);
                    } else if (i == PrivacySettingsActivity.this.callsP2PRow) {
                        new Builder(PrivacySettingsActivity.this.getParentActivity()).setTitle(LocaleController.getString("PrivacyCallsP2PTitle", C0446R.string.PrivacyCallsP2PTitle)).setItems(new String[]{LocaleController.getString("LastSeenEverybody", C0446R.string.LastSeenEverybody), LocaleController.getString("LastSeenContacts", C0446R.string.LastSeenContacts), LocaleController.getString("LastSeenNobody", C0446R.string.LastSeenNobody)}, new C16342()).setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null).show();
                    } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                        view = new BottomSheet.Builder(PrivacySettingsActivity.this.getParentActivity());
                        view.setApplyTopPadding(false);
                        view.setApplyBottomPadding(false);
                        i = new LinearLayout(PrivacySettingsActivity.this.getParentActivity());
                        i.setOrientation(1);
                        int i2 = 0;
                        while (i2 < 2) {
                            String string = i2 == 0 ? LocaleController.getString("PrivacyClearShipping", C0446R.string.PrivacyClearShipping) : i2 == 1 ? LocaleController.getString("PrivacyClearPayment", C0446R.string.PrivacyClearPayment) : null;
                            PrivacySettingsActivity.this.clear[i2] = true;
                            View checkBoxCell = new CheckBoxCell(PrivacySettingsActivity.this.getParentActivity(), 1);
                            checkBoxCell.setTag(Integer.valueOf(i2));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            i.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(string, null, true, true);
                            checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            checkBoxCell.setOnClickListener(new C16353());
                            i2++;
                        }
                        View bottomSheetCell = new BottomSheetCell(PrivacySettingsActivity.this.getParentActivity(), 1);
                        bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        bottomSheetCell.setTextAndIcon(LocaleController.getString("ClearButton", C0446R.string.ClearButton).toUpperCase(), 0);
                        bottomSheetCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        bottomSheetCell.setOnClickListener(new C16374());
                        i.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                        view.setCustomView(i);
                        PrivacySettingsActivity.this.showDialog(view.create());
                    }
                }
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
            if (!(viewHolder == PrivacySettingsActivity.this.passcodeRow || viewHolder == PrivacySettingsActivity.this.passwordRow || viewHolder == PrivacySettingsActivity.this.blockedRow || viewHolder == PrivacySettingsActivity.this.sessionsRow || viewHolder == PrivacySettingsActivity.this.secretWebpageRow || viewHolder == PrivacySettingsActivity.this.webSessionsRow || ((viewHolder == PrivacySettingsActivity.this.groupsRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingGroupInfo()) || ((viewHolder == PrivacySettingsActivity.this.lastSeenRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingLastSeenInfo()) || ((viewHolder == PrivacySettingsActivity.this.callsRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingCallsInfo()) || ((viewHolder == PrivacySettingsActivity.this.deleteAccountRow && !ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo()) || viewHolder == PrivacySettingsActivity.this.paymentsClearRow || viewHolder == PrivacySettingsActivity.this.callsP2PRow)))))) {
                if (viewHolder != PrivacySettingsActivity.this.contactsSyncRow) {
                    return null;
                }
            }
            return true;
        }

        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
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
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == PrivacySettingsActivity.this.blockedRow) {
                        textSettingsCell.setText(LocaleController.getString("BlockedUsers", C0446R.string.BlockedUsers), true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                        textSettingsCell.setText(LocaleController.getString("SessionsTitle", C0446R.string.SessionsTitle), false);
                        return;
                    } else if (i == PrivacySettingsActivity.this.webSessionsRow) {
                        textSettingsCell.setText(LocaleController.getString("WebSessionsTitle", C0446R.string.WebSessionsTitle), false);
                        return;
                    } else if (i == PrivacySettingsActivity.this.passwordRow) {
                        textSettingsCell.setText(LocaleController.getString("TwoStepVerification", C0446R.string.TwoStepVerification), true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                        textSettingsCell.setText(LocaleController.getString("Passcode", C0446R.string.Passcode), true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingLastSeenInfo() != 0) {
                            i = LocaleController.getString("Loading", C0446R.string.Loading);
                        } else {
                            i = PrivacySettingsActivity.this.formatRulesString(0);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", C0446R.string.PrivacyLastSeen), i, true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.callsRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingCallsInfo() != 0) {
                            i = LocaleController.getString("Loading", C0446R.string.Loading);
                        } else {
                            i = PrivacySettingsActivity.this.formatRulesString(2);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Calls", C0446R.string.Calls), i, true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.groupsRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingGroupInfo() != 0) {
                            i = LocaleController.getString("Loading", C0446R.string.Loading);
                        } else {
                            i = PrivacySettingsActivity.this.formatRulesString(1);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", C0446R.string.GroupsAndChannels), i, false);
                        return;
                    } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo() != 0) {
                            i = LocaleController.getString("Loading", C0446R.string.Loading);
                        } else {
                            i = ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getDeleteAccountTTL();
                            if (i <= 182) {
                                i = LocaleController.formatPluralString("Months", i / 30);
                            } else if (i == 365) {
                                i = LocaleController.formatPluralString("Years", i / 365);
                            } else {
                                i = LocaleController.formatPluralString("Days", i);
                            }
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor", C0446R.string.DeleteAccountIfAwayFor), i, false);
                        return;
                    } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyPaymentsClear", C0446R.string.PrivacyPaymentsClear), true);
                        return;
                    } else if (i == PrivacySettingsActivity.this.callsP2PRow) {
                        switch (MessagesController.getMainSettings(PrivacySettingsActivity.this.currentAccount).getInt("calls_p2p_new", MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).defaultP2pContacts)) {
                            case 1:
                                i = LocaleController.getString("LastSeenContacts", C0446R.string.LastSeenContacts);
                                break;
                            case 2:
                                i = LocaleController.getString("LastSeenNobody", C0446R.string.LastSeenNobody);
                                break;
                            default:
                                i = LocaleController.getString("LastSeenEverybody", C0446R.string.LastSeenEverybody);
                                break;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyCallsP2PTitle", C0446R.string.PrivacyCallsP2PTitle), i, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    int access$4300 = PrivacySettingsActivity.this.deleteAccountDetailRow;
                    int i2 = C0446R.drawable.greydivider;
                    if (i == access$4300) {
                        textInfoPrivacyCell.setText(LocaleController.getString("DeleteAccountHelp", C0446R.string.DeleteAccountHelp));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", C0446R.string.GroupsAndChannelsHelp));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("SessionsInfo", C0446R.string.SessionsInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.secretDetailRow) {
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.botsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("PrivacyBotsInfo", C0446R.string.PrivacyBotsInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.callsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", C0446R.string.PrivacyCallsP2PHelp));
                        i = this.mContext;
                        if (PrivacySettingsActivity.this.secretSectionRow == -1) {
                            i2 = C0446R.drawable.greydivider_bottom;
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(i, i2, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PrivacySettingsActivity.this.contactsDetailRow) {
                        if (PrivacySettingsActivity.this.newSync != 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("SyncContactsInfoOn", C0446R.string.SyncContactsInfoOn));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("SyncContactsInfoOff", C0446R.string.SyncContactsInfoOff));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == PrivacySettingsActivity.this.privacySectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", C0446R.string.PrivacyTitle));
                        return;
                    } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                        headerCell.setText(LocaleController.getString("SecurityTitle", C0446R.string.SecurityTitle));
                        return;
                    } else if (i == PrivacySettingsActivity.this.deleteAccountSectionRow) {
                        headerCell.setText(LocaleController.getString("DeleteAccountTitle", C0446R.string.DeleteAccountTitle));
                        return;
                    } else if (i == PrivacySettingsActivity.this.secretSectionRow) {
                        headerCell.setText(LocaleController.getString("SecretChat", C0446R.string.SecretChat));
                        return;
                    } else if (i == PrivacySettingsActivity.this.botsSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyBots", C0446R.string.PrivacyBots));
                        return;
                    } else if (i == PrivacySettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", C0446R.string.Calls));
                        return;
                    } else if (i == PrivacySettingsActivity.this.contactsSectionRow) {
                        headerCell.setText(LocaleController.getString("Contacts", C0446R.string.Contacts));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                        i = LocaleController.getString("SecretWebPage", C0446R.string.SecretWebPage);
                        if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview != 1) {
                            z = false;
                        }
                        textCheckCell.setTextAndCheck(i, z, false);
                        return;
                    } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", C0446R.string.SyncContacts), PrivacySettingsActivity.this.newSync, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (!(i == PrivacySettingsActivity.this.lastSeenRow || i == PrivacySettingsActivity.this.blockedRow || i == PrivacySettingsActivity.this.deleteAccountRow || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.webSessionsRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.groupsRow || i == PrivacySettingsActivity.this.paymentsClearRow)) {
                if (i != PrivacySettingsActivity.this.callsP2PRow) {
                    if (!(i == PrivacySettingsActivity.this.deleteAccountDetailRow || i == PrivacySettingsActivity.this.groupsDetailRow || i == PrivacySettingsActivity.this.sessionsDetailRow || i == PrivacySettingsActivity.this.secretDetailRow || i == PrivacySettingsActivity.this.botsDetailRow || i == PrivacySettingsActivity.this.callsDetailRow)) {
                        if (i != PrivacySettingsActivity.this.contactsDetailRow) {
                            if (!(i == PrivacySettingsActivity.this.securitySectionRow || i == PrivacySettingsActivity.this.deleteAccountSectionRow || i == PrivacySettingsActivity.this.privacySectionRow || i == PrivacySettingsActivity.this.secretSectionRow || i == PrivacySettingsActivity.this.botsSectionRow || i == PrivacySettingsActivity.this.callsSectionRow)) {
                                if (i != PrivacySettingsActivity.this.contactsSectionRow) {
                                    if (i != PrivacySettingsActivity.this.secretWebpageRow) {
                                        if (i != PrivacySettingsActivity.this.contactsSyncRow) {
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

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        boolean z = UserConfig.getInstance(this.currentAccount).syncContacts;
        this.newSync = z;
        this.currentSync = z;
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.botsSectionRow = i;
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
        this.contactsSyncRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsP2PRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsDetailRow = i;
        if (MessagesController.getInstance(this.currentAccount).secretWebpagePreview != 1) {
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        VoIPHelper.upgradeP2pSetting(this.currentAccount);
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
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", C0446R.string.SyncContactsAdded), 0).show();
                }
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", C0446R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new C22461());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
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
        this.listView.setOnItemClickListener(new C22493());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated && this.listAdapter != 0) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private String formatRulesString(int i) {
        i = ContactsController.getInstance(this.currentAccount).getPrivacyRules(i);
        if (i.size() == 0) {
            return LocaleController.getString("LastSeenNobody", C0446R.string.LastSeenNobody);
        }
        int i2 = -1;
        int i3 = 0;
        int i4 = i3;
        int i5 = i4;
        while (i3 < i.size()) {
            PrivacyRule privacyRule = (PrivacyRule) i.get(i3);
            if (privacyRule instanceof TL_privacyValueAllowUsers) {
                i4 += privacyRule.users.size();
            } else if (privacyRule instanceof TL_privacyValueDisallowUsers) {
                i5 += privacyRule.users.size();
            } else {
                i2 = privacyRule instanceof TL_privacyValueAllowAll ? 0 : privacyRule instanceof TL_privacyValueDisallowAll ? 1 : 2;
            }
            i3++;
        }
        if (i2 != 0) {
            if (i2 != -1 || i5 <= 0) {
                if (i2 != 2) {
                    if (i2 != -1 || i5 <= 0 || i4 <= 0) {
                        if (i2 != 1) {
                            if (i4 <= 0) {
                                return "unknown";
                            }
                        }
                        if (i4 == 0) {
                            return LocaleController.getString("LastSeenNobody", C0446R.string.LastSeenNobody);
                        }
                        return LocaleController.formatString("LastSeenNobodyPlus", C0446R.string.LastSeenNobodyPlus, Integer.valueOf(i4));
                    }
                }
                if (i4 == 0 && i5 == 0) {
                    return LocaleController.getString("LastSeenContacts", C0446R.string.LastSeenContacts);
                }
                if (i4 != 0 && i5 != 0) {
                    return LocaleController.formatString("LastSeenContactsMinusPlus", C0446R.string.LastSeenContactsMinusPlus, Integer.valueOf(i5), Integer.valueOf(i4));
                } else if (i5 != 0) {
                    return LocaleController.formatString("LastSeenContactsMinus", C0446R.string.LastSeenContactsMinus, Integer.valueOf(i5));
                } else {
                    return LocaleController.formatString("LastSeenContactsPlus", C0446R.string.LastSeenContactsPlus, Integer.valueOf(i4));
                }
            }
        }
        if (i5 == 0) {
            return LocaleController.getString("LastSeenEverybody", C0446R.string.LastSeenEverybody);
        }
        return LocaleController.formatString("LastSeenEverybodyMinus", C0446R.string.LastSeenEverybodyMinus, Integer.valueOf(i5));
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[20];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r1[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        return r1;
    }
}
