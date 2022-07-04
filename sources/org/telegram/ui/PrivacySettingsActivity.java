package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class PrivacySettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int advancedSectionRow;
    /* access modifiers changed from: private */
    public boolean archiveChats;
    /* access modifiers changed from: private */
    public int blockedRow;
    /* access modifiers changed from: private */
    public int botsDetailRow;
    /* access modifiers changed from: private */
    public int botsSectionRow;
    /* access modifiers changed from: private */
    public int callsRow;
    private boolean[] clear = new boolean[2];
    /* access modifiers changed from: private */
    public int contactsDeleteRow;
    /* access modifiers changed from: private */
    public int contactsDetailRow;
    /* access modifiers changed from: private */
    public int contactsSectionRow;
    /* access modifiers changed from: private */
    public int contactsSuggestRow;
    /* access modifiers changed from: private */
    public int contactsSyncRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    private boolean currentSuggest;
    private boolean currentSync;
    /* access modifiers changed from: private */
    public int deleteAccountDetailRow;
    /* access modifiers changed from: private */
    public int deleteAccountRow;
    /* access modifiers changed from: private */
    public int forwardsRow;
    /* access modifiers changed from: private */
    public int groupsDetailRow;
    /* access modifiers changed from: private */
    public int groupsRow;
    /* access modifiers changed from: private */
    public int lastSeenRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int newChatsHeaderRow;
    /* access modifiers changed from: private */
    public int newChatsRow;
    /* access modifiers changed from: private */
    public int newChatsSectionRow;
    /* access modifiers changed from: private */
    public boolean newSuggest;
    /* access modifiers changed from: private */
    public boolean newSync;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int passportRow;
    /* access modifiers changed from: private */
    public int passwordRow;
    /* access modifiers changed from: private */
    public int paymentsClearRow;
    /* access modifiers changed from: private */
    public int phoneNumberRow;
    /* access modifiers changed from: private */
    public int privacySectionRow;
    /* access modifiers changed from: private */
    public int profilePhotoRow;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int secretDetailRow;
    /* access modifiers changed from: private */
    public int secretMapRow;
    /* access modifiers changed from: private */
    public int secretSectionRow;
    /* access modifiers changed from: private */
    public int secretWebpageRow;
    /* access modifiers changed from: private */
    public int securitySectionRow;
    /* access modifiers changed from: private */
    public int sessionsDetailRow;
    /* access modifiers changed from: private */
    public int sessionsRow;
    /* access modifiers changed from: private */
    public int webSessionsRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getContactsController().loadPrivacySettings();
        getMessagesController().getBlockedPeers(true);
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        boolean z2 = getUserConfig().suggestContacts;
        this.newSuggest = z2;
        this.currentSuggest = z2;
        TLRPC.TL_globalPrivacySettings privacySettings = getContactsController().getGlobalPrivacySettings();
        if (privacySettings != null) {
            this.archiveChats = privacySettings.archive_and_mute_new_noncontact_peers;
        }
        updateRows();
        loadPasswordSettings();
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        return true;
    }

    public void onFragmentDestroy() {
        boolean z;
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        boolean save = false;
        if (this.currentSync != this.newSync) {
            getUserConfig().syncContacts = this.newSync;
            save = true;
            if (this.newSync) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", NUM), 0).show();
                }
            }
        }
        boolean z2 = this.newSuggest;
        if (z2 != this.currentSuggest) {
            if (!z2) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            save = true;
            TLRPC.TL_contacts_toggleTopPeers req = new TLRPC.TL_contacts_toggleTopPeers();
            req.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(req, PrivacySettingsActivity$$ExternalSyntheticLambda6.INSTANCE);
        }
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        if (!(globalPrivacySettings == null || globalPrivacySettings.archive_and_mute_new_noncontact_peers == (z = this.archiveChats))) {
            globalPrivacySettings.archive_and_mute_new_noncontact_peers = z;
            save = true;
            TLRPC.TL_account_setGlobalPrivacySettings req2 = new TLRPC.TL_account_setGlobalPrivacySettings();
            req2.settings = new TLRPC.TL_globalPrivacySettings();
            req2.settings.flags |= 1;
            req2.settings.archive_and_mute_new_noncontact_peers = this.archiveChats;
            getConnectionsManager().sendRequest(req2, PrivacySettingsActivity$$ExternalSyntheticLambda7.INSTANCE);
        }
        if (save) {
            getUserConfig().saveConfig(false);
        }
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onFragmentDestroy$1(TLObject response, TLRPC.TL_error error) {
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacySettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        AnonymousClass2 r3 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r3;
        recyclerListView.setLayoutManager(r3);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacySettingsActivity$$ExternalSyntheticLambda8(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4371lambda$createView$15$orgtelegramuiPrivacySettingsActivity(View view, int position) {
        String name;
        int type;
        int selected;
        View view2 = view;
        int i = position;
        if (view.isEnabled()) {
            if (i == this.blockedRow) {
                presentFragment(new PrivacyUsersActivity());
                return;
            }
            boolean z = false;
            if (i == this.sessionsRow) {
                presentFragment(new SessionsActivity(0));
            } else if (i == this.webSessionsRow) {
                presentFragment(new SessionsActivity(1));
            } else {
                if (i == this.deleteAccountRow) {
                    if (getParentActivity() != null) {
                        int ttl = getContactsController().getDeleteAccountTTL();
                        if (ttl <= 31) {
                            selected = 0;
                        } else if (ttl <= 93) {
                            selected = 1;
                        } else if (ttl <= 182) {
                            selected = 2;
                        } else {
                            selected = 3;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("DeleteAccountTitle", NUM));
                        String[] items = {LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
                        LinearLayout linearLayout = new LinearLayout(getParentActivity());
                        linearLayout.setOrientation(1);
                        builder.setView(linearLayout);
                        int a = 0;
                        while (a < items.length) {
                            RadioColorCell cell = new RadioColorCell(getParentActivity());
                            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            cell.setTag(Integer.valueOf(a));
                            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                            cell.setTextAndValue(items[a], selected == a);
                            linearLayout.addView(cell);
                            cell.setOnClickListener(new PrivacySettingsActivity$$ExternalSyntheticLambda13(this, builder));
                            a++;
                        }
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                    }
                } else if (i == this.lastSeenRow) {
                    presentFragment(new PrivacyControlActivity(0));
                } else if (i == this.phoneNumberRow) {
                    presentFragment(new PrivacyControlActivity(6));
                } else if (i == this.groupsRow) {
                    presentFragment(new PrivacyControlActivity(1));
                } else if (i == this.callsRow) {
                    presentFragment(new PrivacyControlActivity(2));
                } else if (i == this.profilePhotoRow) {
                    presentFragment(new PrivacyControlActivity(4));
                } else if (i == this.forwardsRow) {
                    presentFragment(new PrivacyControlActivity(5));
                } else if (i == this.passwordRow) {
                    TLRPC.TL_account_password tL_account_password = this.currentPassword;
                    if (tL_account_password != null) {
                        if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                        }
                        if (this.currentPassword.has_password) {
                            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
                            fragment.setPassword(this.currentPassword);
                            presentFragment(fragment);
                            return;
                        }
                        if (TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                            type = 6;
                        } else {
                            type = 5;
                        }
                        presentFragment(new TwoStepVerificationSetupActivity(type, this.currentPassword));
                    }
                } else if (i == this.passcodeRow) {
                    presentFragment(PasscodeActivity.determineOpenFragment());
                } else if (i == this.secretWebpageRow) {
                    if (getMessagesController().secretWebpagePreview == 1) {
                        getMessagesController().secretWebpagePreview = 0;
                    } else {
                        getMessagesController().secretWebpagePreview = 1;
                    }
                    MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", getMessagesController().secretWebpagePreview).commit();
                    if (view2 instanceof TextCheckCell) {
                        TextCheckCell textCheckCell = (TextCheckCell) view2;
                        if (getMessagesController().secretWebpagePreview == 1) {
                            z = true;
                        }
                        textCheckCell.setChecked(z);
                    }
                } else if (i == this.contactsDeleteRow) {
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                        builder2.setTitle(LocaleController.getString("SyncContactsDeleteTitle", NUM));
                        builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", NUM)));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new PrivacySettingsActivity$$ExternalSyntheticLambda10(this));
                        AlertDialog alertDialog = builder2.create();
                        showDialog(alertDialog);
                        TextView button = (TextView) alertDialog.getButton(-1);
                        if (button != null) {
                            button.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                } else if (i == this.contactsSuggestRow) {
                    TextCheckCell cell2 = (TextCheckCell) view2;
                    if (this.newSuggest) {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
                        builder3.setTitle(LocaleController.getString("SuggestContactsTitle", NUM));
                        builder3.setMessage(LocaleController.getString("SuggestContactsAlert", NUM));
                        builder3.setPositiveButton(LocaleController.getString("MuteDisable", NUM), new PrivacySettingsActivity$$ExternalSyntheticLambda11(this, cell2));
                        builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog alertDialog2 = builder3.create();
                        showDialog(alertDialog2);
                        TextView button2 = (TextView) alertDialog2.getButton(-1);
                        if (button2 != null) {
                            button2.setTextColor(Theme.getColor("dialogTextRed2"));
                            return;
                        }
                        return;
                    }
                    this.newSuggest = true;
                    cell2.setChecked(true);
                } else if (i == this.newChatsRow) {
                    boolean z2 = !this.archiveChats;
                    this.archiveChats = z2;
                    ((TextCheckCell) view2).setChecked(z2);
                } else if (i == this.contactsSyncRow) {
                    boolean z3 = !this.newSync;
                    this.newSync = z3;
                    if (view2 instanceof TextCheckCell) {
                        ((TextCheckCell) view2).setChecked(z3);
                    }
                } else if (i == this.secretMapRow) {
                    AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new PrivacySettingsActivity$$ExternalSyntheticLambda14(this), false, (Theme.ResourcesProvider) null);
                } else if (i == this.paymentsClearRow) {
                    AlertDialog.Builder builder4 = new AlertDialog.Builder((Context) getParentActivity());
                    builder4.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
                    builder4.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", NUM));
                    LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                    linearLayout2.setOrientation(1);
                    builder4.setView(linearLayout2);
                    int a2 = 0;
                    for (int i2 = 2; a2 < i2; i2 = 2) {
                        if (a2 == 0) {
                            name = LocaleController.getString("PrivacyClearShipping", NUM);
                        } else {
                            name = LocaleController.getString("PrivacyClearPayment", NUM);
                        }
                        this.clear[a2] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21, (Theme.ResourcesProvider) null);
                        checkBoxCell.setTag(Integer.valueOf(a2));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        linearLayout2.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(name, (String) null, true, false);
                        checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                        checkBoxCell.setOnClickListener(new PrivacySettingsActivity$$ExternalSyntheticLambda12(this));
                        a2++;
                    }
                    builder4.setPositiveButton(LocaleController.getString("ClearButton", NUM), new PrivacySettingsActivity$$ExternalSyntheticLambda9(this));
                    builder4.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder4.create());
                    AlertDialog alertDialog3 = builder4.create();
                    showDialog(alertDialog3);
                    TextView button3 = (TextView) alertDialog3.getButton(-1);
                    if (button3 != null) {
                        button3.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (i == this.passportRow) {
                    presentFragment(new PassportActivity(5, 0, "", "", (String) null, (String) null, (String) null, (TLRPC.TL_account_authorizationForm) null, (TLRPC.TL_account_password) null));
                }
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4374lambda$createView$4$orgtelegramuiPrivacySettingsActivity(AlertDialog.Builder builder, View v) {
        builder.getDismissRunnable().run();
        Integer which = (Integer) v.getTag();
        int value = 0;
        if (which.intValue() == 0) {
            value = 30;
        } else if (which.intValue() == 1) {
            value = 90;
        } else if (which.intValue() == 2) {
            value = 182;
        } else if (which.intValue() == 3) {
            value = 365;
        }
        AlertDialog progressDialog2 = new AlertDialog(getParentActivity(), 3);
        progressDialog2.setCanCancel(false);
        progressDialog2.show();
        TLRPC.TL_account_setAccountTTL req = new TLRPC.TL_account_setAccountTTL();
        req.ttl = new TLRPC.TL_accountDaysTTL();
        req.ttl.days = value;
        getConnectionsManager().sendRequest(req, new PrivacySettingsActivity$$ExternalSyntheticLambda3(this, progressDialog2, req));
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4373lambda$createView$3$orgtelegramuiPrivacySettingsActivity(AlertDialog progressDialog2, TLRPC.TL_account_setAccountTTL req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda17(this, progressDialog2, response, req));
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4372lambda$createView$2$orgtelegramuiPrivacySettingsActivity(AlertDialog progressDialog2, TLObject response, TLRPC.TL_account_setAccountTTL req) {
        try {
            progressDialog2.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_boolTrue) {
            getContactsController().setDeleteAccountTTL(req.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4376lambda$createView$6$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        AlertDialog show = new AlertDialog.Builder(getParentActivity(), 3, (Theme.ResourcesProvider) null).show();
        this.progressDialog = show;
        show.setCanCancel(false);
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z = this.newSync;
            userConfig.syncContacts = z;
            this.currentSync = z;
            getUserConfig().saveConfig(false);
        }
        getContactsController().deleteAllContacts(new PrivacySettingsActivity$$ExternalSyntheticLambda15(this));
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4375lambda$createView$5$orgtelegramuiPrivacySettingsActivity() {
        this.progressDialog.dismiss();
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4379lambda$createView$9$orgtelegramuiPrivacySettingsActivity(TextCheckCell cell, DialogInterface dialogInterface, int i) {
        TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(req, new PrivacySettingsActivity$$ExternalSyntheticLambda4(this, cell));
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4378lambda$createView$8$orgtelegramuiPrivacySettingsActivity(TextCheckCell cell, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda1(this, cell));
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4377lambda$createView$7$orgtelegramuiPrivacySettingsActivity(TextCheckCell cell) {
        boolean z = !this.newSuggest;
        this.newSuggest = z;
        cell.setChecked(z);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4367lambda$createView$10$orgtelegramuiPrivacySettingsActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4368lambda$createView$11$orgtelegramuiPrivacySettingsActivity(View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[num] = !zArr[num];
        cell.setChecked(zArr[num], true);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4370lambda$createView$14$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) getParentActivity());
        builder1.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
        builder1.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", NUM));
        builder1.setPositiveButton(LocaleController.getString("ClearButton", NUM), new PrivacySettingsActivity$$ExternalSyntheticLambda0(this));
        builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder1.create());
        AlertDialog alertDialog = builder1.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4369lambda$createView$13$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface2, int i2) {
        String text;
        TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(req, PrivacySettingsActivity$$ExternalSyntheticLambda5.INSTANCE);
        boolean[] zArr = this.clear;
        if (zArr[0] && zArr[1]) {
            text = LocaleController.getString("PrivacyPaymentsPaymentShippingCleared", NUM);
        } else if (zArr[0]) {
            text = LocaleController.getString("PrivacyPaymentsShippingInfoCleared", NUM);
        } else if (zArr[1]) {
            text = LocaleController.getString("PrivacyPaymentsPaymentInfoCleared", NUM);
        } else {
            return;
        }
        BulletinFactory.of(this).createSimpleBulletin(NUM, text).show();
    }

    static /* synthetic */ void lambda$createView$12(TLObject response, TLRPC.TL_error error) {
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            TLRPC.TL_globalPrivacySettings privacySettings = getContactsController().getGlobalPrivacySettings();
            if (privacySettings != null) {
                this.archiveChats = privacySettings.archive_and_mute_new_noncontact_peers;
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.listAdapter.notifyItemChanged(this.blockedRow);
        } else if (id != NotificationCenter.didSetOrRemoveTwoStepPassword) {
        } else {
            if (args.length > 0) {
                this.currentPassword = args[0];
                ListAdapter listAdapter3 = this.listAdapter;
                if (listAdapter3 != null) {
                    listAdapter3.notifyItemChanged(this.passwordRow);
                    return;
                }
                return;
            }
            this.currentPassword = null;
            loadPasswordSettings();
            updateRows();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.privacySectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.blockedRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.phoneNumberRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.lastSeenRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.profilePhotoRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.forwardsRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.callsRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.groupsRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.groupsDetailRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.securitySectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.passcodeRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.passwordRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.sessionsRow = i12;
        this.rowCount = i13 + 1;
        this.sessionsDetailRow = i13;
        if (getMessagesController().autoarchiveAvailable || getUserConfig().isPremium()) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.newChatsHeaderRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.newChatsRow = i15;
            this.rowCount = i16 + 1;
            this.newChatsSectionRow = i16;
        } else {
            this.newChatsHeaderRow = -1;
            this.newChatsRow = -1;
            this.newChatsSectionRow = -1;
        }
        int i17 = this.rowCount;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.advancedSectionRow = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.deleteAccountRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.deleteAccountDetailRow = i19;
        this.rowCount = i20 + 1;
        this.botsSectionRow = i20;
        if (getUserConfig().hasSecureData) {
            int i21 = this.rowCount;
            this.rowCount = i21 + 1;
            this.passportRow = i21;
        } else {
            this.passportRow = -1;
        }
        int i22 = this.rowCount;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.paymentsClearRow = i22;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.webSessionsRow = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.botsDetailRow = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.contactsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactsDeleteRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.contactsSyncRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.contactsSuggestRow = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.contactsDetailRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.secretSectionRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.secretMapRow = i31;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.secretWebpageRow = i32;
        this.rowCount = i33 + 1;
        this.secretDetailRow = i33;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        getConnectionsManager().sendRequest(new TLRPC.TL_account_getPassword(), new PrivacySettingsActivity$$ExternalSyntheticLambda2(this), 10);
    }

    /* renamed from: lambda$loadPasswordSettings$17$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4381xbd2153e9(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda16(this, (TLRPC.TL_account_password) response));
        }
    }

    /* renamed from: lambda$loadPasswordSettings$16$org-telegram-ui-PrivacySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m4380x308128e8(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        TwoStepVerificationActivity.initPasswordNewAlgo(password);
        if (getUserConfig().hasSecureData || !password.has_secure_values) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(this.passwordRow);
                return;
            }
            return;
        }
        getUserConfig().hasSecureData = true;
        getUserConfig().saveConfig(false);
        updateRows();
    }

    public static String formatRulesString(AccountInstance accountInstance, int rulesType) {
        int i = rulesType;
        ArrayList<TLRPC.PrivacyRule> privacyRules = accountInstance.getContactsController().getPrivacyRules(i);
        if (privacyRules.size() != 0) {
            int type = -1;
            int plus = 0;
            int minus = 0;
            for (int a = 0; a < privacyRules.size(); a++) {
                TLRPC.PrivacyRule rule = privacyRules.get(a);
                if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants participants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                    int N = participants.chats.size();
                    for (int b = 0; b < N; b++) {
                        TLRPC.Chat chat = accountInstance.getMessagesController().getChat(participants.chats.get(b));
                        if (chat != null) {
                            plus += chat.participants_count;
                        }
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants participants2 = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                    int N2 = participants2.chats.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        TLRPC.Chat chat2 = accountInstance.getMessagesController().getChat(participants2.chats.get(b2));
                        if (chat2 != null) {
                            minus += chat2.participants_count;
                        }
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    plus += ((TLRPC.TL_privacyValueAllowUsers) rule).users.size();
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    minus += ((TLRPC.TL_privacyValueDisallowUsers) rule).users.size();
                } else if (type == -1) {
                    if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                        type = 0;
                    } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            if (type == 0 || (type == -1 && minus > 0)) {
                if (i == 3) {
                    if (minus == 0) {
                        return LocaleController.getString("P2PEverybody", NUM);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", NUM, Integer.valueOf(minus));
                } else if (minus == 0) {
                    return LocaleController.getString("LastSeenEverybody", NUM);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", NUM, Integer.valueOf(minus));
                }
            } else if (type == 2 || (type == -1 && minus > 0 && plus > 0)) {
                if (i == 3) {
                    if (plus == 0 && minus == 0) {
                        return LocaleController.getString("P2PContacts", NUM);
                    }
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("P2PContactsMinusPlus", NUM, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("P2PContactsMinus", NUM, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("P2PContactsPlus", NUM, Integer.valueOf(plus));
                    }
                } else if (plus == 0 && minus == 0) {
                    return LocaleController.getString("LastSeenContacts", NUM);
                } else {
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinusPlus", NUM, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinus", NUM, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("LastSeenContactsPlus", NUM, Integer.valueOf(plus));
                    }
                }
            } else if (type != 1 && plus <= 0) {
                return "unknown";
            } else {
                if (i == 3) {
                    if (plus == 0) {
                        return LocaleController.getString("P2PNobody", NUM);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", NUM, Integer.valueOf(plus));
                } else if (plus == 0) {
                    return LocaleController.getString("LastSeenNobody", NUM);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", NUM, Integer.valueOf(plus));
                }
            }
        } else if (i == 3) {
            return LocaleController.getString("P2PNobody", NUM);
        } else {
            return LocaleController.getString("LastSeenNobody", NUM);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.webSessionsRow || (position == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) || ((position == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) || ((position == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) || ((position == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) || ((position == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) || ((position == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) || ((position == PrivacySettingsActivity.this.deleteAccountRow && !PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) || ((position == PrivacySettingsActivity.this.newChatsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingGlobalSettings()) || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.contactsDeleteRow || position == PrivacySettingsActivity.this.contactsSuggestRow)))))));
        }

        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    boolean showLoading = false;
                    String value2 = null;
                    int loadingLen = 16;
                    boolean animated = holder.itemView.getTag() != null && ((Integer) holder.itemView.getTag()).intValue() == position;
                    holder.itemView.setTag(Integer.valueOf(position));
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == PrivacySettingsActivity.this.blockedRow) {
                        int totalCount = PrivacySettingsActivity.this.getMessagesController().totalBlockedCount;
                        if (totalCount == 0) {
                            textCell.setTextAndValue(LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("BlockedEmpty", NUM), true);
                        } else if (totalCount > 0) {
                            textCell.setTextAndValue(LocaleController.getString("BlockedUsers", NUM), String.format("%d", new Object[]{Integer.valueOf(totalCount)}), true);
                        } else {
                            showLoading = true;
                            textCell.setText(LocaleController.getString("BlockedUsers", NUM), true);
                        }
                    } else if (position == PrivacySettingsActivity.this.sessionsRow) {
                        textCell.setText(LocaleController.getString("SessionsTitle", NUM), false);
                    } else if (position == PrivacySettingsActivity.this.webSessionsRow) {
                        textCell.setText(LocaleController.getString("WebSessionsTitle", NUM), false);
                    } else if (position == PrivacySettingsActivity.this.passwordRow) {
                        if (PrivacySettingsActivity.this.currentPassword == null) {
                            showLoading = true;
                        } else if (PrivacySettingsActivity.this.currentPassword.has_password) {
                            value2 = LocaleController.getString("PasswordOn", NUM);
                        } else {
                            value2 = LocaleController.getString("PasswordOff", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("TwoStepVerification", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.passcodeRow) {
                        textCell.setText(LocaleController.getString("Passcode", NUM), true);
                    } else if (position == PrivacySettingsActivity.this.phoneNumberRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyPhone", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.lastSeenRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.groupsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                        }
                        textCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", NUM), value2, false);
                    } else if (position == PrivacySettingsActivity.this.callsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Calls", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.profilePhotoRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.forwardsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) {
                            showLoading = true;
                            loadingLen = 30;
                        } else {
                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyForwards", NUM), value2, true);
                    } else if (position == PrivacySettingsActivity.this.passportRow) {
                        textCell.setText(LocaleController.getString("TelegramPassport", NUM), true);
                    } else if (position == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                            showLoading = true;
                        } else {
                            int ttl = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                            if (ttl <= 182) {
                                value2 = LocaleController.formatPluralString("Months", ttl / 30, new Object[0]);
                            } else if (ttl == 365) {
                                value2 = LocaleController.formatPluralString("Years", ttl / 365, new Object[0]);
                            } else {
                                value2 = LocaleController.formatPluralString("Days", ttl, new Object[0]);
                            }
                        }
                        textCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor3", NUM), value2, false);
                    } else if (position == PrivacySettingsActivity.this.paymentsClearRow) {
                        textCell.setText(LocaleController.getString("PrivacyPaymentsClear", NUM), true);
                    } else if (position == PrivacySettingsActivity.this.secretMapRow) {
                        switch (SharedConfig.mapPreviewType) {
                            case 0:
                                value = LocaleController.getString("MapPreviewProviderTelegram", NUM);
                                break;
                            case 1:
                                value = LocaleController.getString("MapPreviewProviderGoogle", NUM);
                                break;
                            case 2:
                                value = LocaleController.getString("MapPreviewProviderNobody", NUM);
                                break;
                            default:
                                value = LocaleController.getString("MapPreviewProviderYandex", NUM);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", NUM), value, true);
                    } else if (position == PrivacySettingsActivity.this.contactsDeleteRow) {
                        textCell.setText(LocaleController.getString("SyncContactsDelete", NUM), true);
                    }
                    textCell.setDrawLoading(showLoading, loadingLen, animated);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                        privacyCell.setText(LocaleController.getString("DeleteAccountHelp", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.groupsDetailRow) {
                        privacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.sessionsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SessionsInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretDetailRow) {
                        privacyCell.setText(LocaleController.getString("SecretWebPageInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.botsDetailRow) {
                        privacyCell.setText(LocaleController.getString("PrivacyBotsInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsDetailRow) {
                        privacyCell.setText(LocaleController.getString("SuggestContactsInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PrivacySettingsActivity.this.newChatsSectionRow) {
                        privacyCell.setText(LocaleController.getString("ArchiveAndMuteInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == PrivacySettingsActivity.this.privacySectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.securitySectionRow) {
                        headerCell.setText(LocaleController.getString("SecurityTitle", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.advancedSectionRow) {
                        headerCell.setText(LocaleController.getString("DeleteMyAccount", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.secretSectionRow) {
                        headerCell.setText(LocaleController.getString("SecretChat", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.botsSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyBots", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSectionRow) {
                        headerCell.setText(LocaleController.getString("Contacts", NUM));
                        return;
                    } else if (position == PrivacySettingsActivity.this.newChatsHeaderRow) {
                        headerCell.setText(LocaleController.getString("NewChatsFromNonContacts", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == PrivacySettingsActivity.this.secretWebpageRow) {
                        String string = LocaleController.getString("SecretWebPage", NUM);
                        if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                            z = false;
                        }
                        textCheckCell.setTextAndCheck(string, z, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSyncRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", NUM), PrivacySettingsActivity.this.newSync, true);
                        return;
                    } else if (position == PrivacySettingsActivity.this.contactsSuggestRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", NUM), PrivacySettingsActivity.this.newSuggest, false);
                        return;
                    } else if (position == PrivacySettingsActivity.this.newChatsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveAndMute", NUM), PrivacySettingsActivity.this.archiveChats, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.lastSeenRow || position == PrivacySettingsActivity.this.phoneNumberRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.deleteAccountRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.webSessionsRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.groupsRow || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsDeleteRow) {
                return 0;
            }
            if (position == PrivacySettingsActivity.this.deleteAccountDetailRow || position == PrivacySettingsActivity.this.groupsDetailRow || position == PrivacySettingsActivity.this.sessionsDetailRow || position == PrivacySettingsActivity.this.secretDetailRow || position == PrivacySettingsActivity.this.botsDetailRow || position == PrivacySettingsActivity.this.contactsDetailRow || position == PrivacySettingsActivity.this.newChatsSectionRow) {
                return 1;
            }
            if (position == PrivacySettingsActivity.this.securitySectionRow || position == PrivacySettingsActivity.this.advancedSectionRow || position == PrivacySettingsActivity.this.privacySectionRow || position == PrivacySettingsActivity.this.secretSectionRow || position == PrivacySettingsActivity.this.botsSectionRow || position == PrivacySettingsActivity.this.contactsSectionRow || position == PrivacySettingsActivity.this.newChatsHeaderRow) {
                return 2;
            }
            if (position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.contactsSuggestRow || position == PrivacySettingsActivity.this.newChatsRow) {
                return 3;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        return themeDescriptions;
    }
}
