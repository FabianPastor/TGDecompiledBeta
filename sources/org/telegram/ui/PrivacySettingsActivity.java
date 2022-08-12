package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$PrivacyRule;
import org.telegram.tgnet.TLRPC$TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC$TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC$TL_account_setGlobalPrivacySettings;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_contacts_toggleTopPeers;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_globalPrivacySettings;
import org.telegram.tgnet.TLRPC$TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowChatParticipants;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowUsers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
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
    public TLRPC$TL_account_password currentPassword;
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
    public int privacyShadowRow;
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
    public int voicesRow;
    /* access modifiers changed from: private */
    public int webSessionsRow;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onFragmentDestroy$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

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
        TLRPC$TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        if (globalPrivacySettings != null) {
            this.archiveChats = globalPrivacySettings.archive_and_mute_new_noncontact_peers;
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
        boolean z2;
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        boolean z3 = true;
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z4 = this.newSync;
            userConfig.syncContacts = z4;
            if (z4) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", R.string.SyncContactsAdded), 0).show();
                }
            }
            z = true;
        } else {
            z = false;
        }
        boolean z5 = this.newSuggest;
        if (z5 != this.currentSuggest) {
            if (!z5) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            TLRPC$TL_contacts_toggleTopPeers tLRPC$TL_contacts_toggleTopPeers = new TLRPC$TL_contacts_toggleTopPeers();
            tLRPC$TL_contacts_toggleTopPeers.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_toggleTopPeers, PrivacySettingsActivity$$ExternalSyntheticLambda16.INSTANCE);
            z = true;
        }
        TLRPC$TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        if (globalPrivacySettings == null || globalPrivacySettings.archive_and_mute_new_noncontact_peers == (z2 = this.archiveChats)) {
            z3 = z;
        } else {
            globalPrivacySettings.archive_and_mute_new_noncontact_peers = z2;
            TLRPC$TL_account_setGlobalPrivacySettings tLRPC$TL_account_setGlobalPrivacySettings = new TLRPC$TL_account_setGlobalPrivacySettings();
            TLRPC$TL_globalPrivacySettings tLRPC$TL_globalPrivacySettings = new TLRPC$TL_globalPrivacySettings();
            tLRPC$TL_account_setGlobalPrivacySettings.settings = tLRPC$TL_globalPrivacySettings;
            tLRPC$TL_globalPrivacySettings.flags |= 1;
            tLRPC$TL_globalPrivacySettings.archive_and_mute_new_noncontact_peers = this.archiveChats;
            getConnectionsManager().sendRequest(tLRPC$TL_account_setGlobalPrivacySettings, PrivacySettingsActivity$$ExternalSyntheticLambda14.INSTANCE);
        }
        if (z3) {
            getUserConfig().saveConfig(false);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivacySettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this, context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacySettingsActivity$$ExternalSyntheticLambda17(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(View view, int i) {
        String str;
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
                int i2 = 6;
                if (i == this.deleteAccountRow) {
                    if (getParentActivity() != null) {
                        int deleteAccountTTL = getContactsController().getDeleteAccountTTL();
                        int i3 = deleteAccountTTL <= 31 ? 0 : deleteAccountTTL <= 93 ? 1 : deleteAccountTTL <= 182 ? 2 : 3;
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("DeleteAccountTitle", R.string.DeleteAccountTitle));
                        String[] strArr = {LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
                        LinearLayout linearLayout = new LinearLayout(getParentActivity());
                        linearLayout.setOrientation(1);
                        builder.setView(linearLayout);
                        int i4 = 0;
                        while (i4 < 4) {
                            RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            radioColorCell.setTag(Integer.valueOf(i4));
                            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                            radioColorCell.setTextAndValue(strArr[i4], i3 == i4);
                            linearLayout.addView(radioColorCell);
                            radioColorCell.setOnClickListener(new PrivacySettingsActivity$$ExternalSyntheticLambda5(this, builder));
                            i4++;
                        }
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
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
                } else if (i == this.voicesRow) {
                    if (!getUserConfig().isPremium()) {
                        try {
                            this.fragmentView.performHapticFeedback(3, 2);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        BulletinFactory.of(this).createRestrictVoiceMessagesPremiumBulletin().show();
                        return;
                    }
                    presentFragment(new PrivacyControlActivity(8));
                } else if (i == this.passwordRow) {
                    TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                    if (tLRPC$TL_account_password != null) {
                        if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                        }
                        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                        if (tLRPC$TL_account_password2.has_password) {
                            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                            twoStepVerificationActivity.setPassword(this.currentPassword);
                            presentFragment(twoStepVerificationActivity);
                            return;
                        }
                        if (!TextUtils.isEmpty(tLRPC$TL_account_password2.email_unconfirmed_pattern)) {
                            i2 = 5;
                        }
                        presentFragment(new TwoStepVerificationSetupActivity(i2, this.currentPassword));
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
                    if (view instanceof TextCheckCell) {
                        TextCheckCell textCheckCell = (TextCheckCell) view;
                        if (getMessagesController().secretWebpagePreview == 1) {
                            z = true;
                        }
                        textCheckCell.setChecked(z);
                    }
                } else if (i == this.contactsDeleteRow) {
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                        builder2.setTitle(LocaleController.getString("SyncContactsDeleteTitle", R.string.SyncContactsDeleteTitle));
                        builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", R.string.SyncContactsDeleteText)));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new PrivacySettingsActivity$$ExternalSyntheticLambda0(this));
                        AlertDialog create = builder2.create();
                        showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                } else if (i == this.contactsSuggestRow) {
                    TextCheckCell textCheckCell2 = (TextCheckCell) view;
                    if (this.newSuggest) {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
                        builder3.setTitle(LocaleController.getString("SuggestContactsTitle", R.string.SuggestContactsTitle));
                        builder3.setMessage(LocaleController.getString("SuggestContactsAlert", R.string.SuggestContactsAlert));
                        builder3.setPositiveButton(LocaleController.getString("MuteDisable", R.string.MuteDisable), new PrivacySettingsActivity$$ExternalSyntheticLambda3(this, textCheckCell2));
                        builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        AlertDialog create2 = builder3.create();
                        showDialog(create2);
                        TextView textView2 = (TextView) create2.getButton(-1);
                        if (textView2 != null) {
                            textView2.setTextColor(Theme.getColor("dialogTextRed2"));
                            return;
                        }
                        return;
                    }
                    this.newSuggest = true;
                    textCheckCell2.setChecked(true);
                } else if (i == this.newChatsRow) {
                    boolean z2 = !this.archiveChats;
                    this.archiveChats = z2;
                    ((TextCheckCell) view).setChecked(z2);
                } else if (i == this.contactsSyncRow) {
                    boolean z3 = !this.newSync;
                    this.newSync = z3;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(z3);
                    }
                } else if (i == this.secretMapRow) {
                    AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new PrivacySettingsActivity$$ExternalSyntheticLambda7(this), false, (Theme.ResourcesProvider) null);
                } else if (i == this.paymentsClearRow) {
                    AlertDialog.Builder builder4 = new AlertDialog.Builder((Context) getParentActivity());
                    builder4.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
                    builder4.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", R.string.PrivacyPaymentsClearAlertText));
                    LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                    linearLayout2.setOrientation(1);
                    builder4.setView(linearLayout2);
                    for (int i5 = 0; i5 < 2; i5++) {
                        if (i5 == 0) {
                            str = LocaleController.getString("PrivacyClearShipping", R.string.PrivacyClearShipping);
                        } else {
                            str = LocaleController.getString("PrivacyClearPayment", R.string.PrivacyClearPayment);
                        }
                        this.clear[i5] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21, (Theme.ResourcesProvider) null);
                        checkBoxCell.setTag(Integer.valueOf(i5));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        linearLayout2.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(str, (String) null, true, false);
                        checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                        checkBoxCell.setOnClickListener(new PrivacySettingsActivity$$ExternalSyntheticLambda4(this));
                    }
                    builder4.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new PrivacySettingsActivity$$ExternalSyntheticLambda2(this));
                    builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder4.create());
                    AlertDialog create3 = builder4.create();
                    showDialog(create3);
                    TextView textView3 = (TextView) create3.getButton(-1);
                    if (textView3 != null) {
                        textView3.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (i == this.passportRow) {
                    presentFragment(new PassportActivity(5, 0, "", "", (String) null, (String) null, (String) null, (TLRPC$TL_account_authorizationForm) null, (TLRPC$TL_account_password) null));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(AlertDialog.Builder builder, View view) {
        int i;
        builder.getDismissRunnable().run();
        Integer num = (Integer) view.getTag();
        if (num.intValue() == 0) {
            i = 30;
        } else if (num.intValue() == 1) {
            i = 90;
        } else if (num.intValue() == 2) {
            i = 182;
        } else {
            i = num.intValue() == 3 ? 365 : 0;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL = new TLRPC$TL_account_setAccountTTL();
        TLRPC$TL_accountDaysTTL tLRPC$TL_accountDaysTTL = new TLRPC$TL_accountDaysTTL();
        tLRPC$TL_account_setAccountTTL.ttl = tLRPC$TL_accountDaysTTL;
        tLRPC$TL_accountDaysTTL.days = i;
        getConnectionsManager().sendRequest(tLRPC$TL_account_setAccountTTL, new PrivacySettingsActivity$$ExternalSyntheticLambda12(this, alertDialog, tLRPC$TL_account_setAccountTTL));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(AlertDialog alertDialog, TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda9(this, alertDialog, tLObject, tLRPC$TL_account_setAccountTTL));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            getContactsController().setDeleteAccountTTL(tLRPC$TL_account_setAccountTTL.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
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
        getContactsController().deleteAllContacts(new PrivacySettingsActivity$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5() {
        this.progressDialog.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(TextCheckCell textCheckCell, DialogInterface dialogInterface, int i) {
        TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tLRPC$TL_payments_clearSavedInfo.credentials = zArr[1];
        tLRPC$TL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tLRPC$TL_payments_clearSavedInfo, new PrivacySettingsActivity$$ExternalSyntheticLambda13(this, textCheckCell));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(TextCheckCell textCheckCell, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda10(this, textCheckCell));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(TextCheckCell textCheckCell) {
        boolean z = !this.newSuggest;
        this.newSuggest = z;
        textCheckCell.setChecked(z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10() {
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[intValue] = !zArr[intValue];
        checkBoxCell.setChecked(zArr[intValue], true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(DialogInterface dialogInterface, int i) {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
        builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", R.string.PrivacyPaymentsClearAlert));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new PrivacySettingsActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(DialogInterface dialogInterface, int i) {
        String str;
        TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tLRPC$TL_payments_clearSavedInfo.credentials = zArr[1];
        tLRPC$TL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tLRPC$TL_payments_clearSavedInfo, PrivacySettingsActivity$$ExternalSyntheticLambda15.INSTANCE);
        boolean[] zArr2 = this.clear;
        if (zArr2[0] && zArr2[1]) {
            str = LocaleController.getString("PrivacyPaymentsPaymentShippingCleared", R.string.PrivacyPaymentsPaymentShippingCleared);
        } else if (zArr2[0]) {
            str = LocaleController.getString("PrivacyPaymentsShippingInfoCleared", R.string.PrivacyPaymentsShippingInfoCleared);
        } else if (zArr2[1]) {
            str = LocaleController.getString("PrivacyPaymentsPaymentInfoCleared", R.string.PrivacyPaymentsPaymentInfoCleared);
        } else {
            return;
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, str).show();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            TLRPC$TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
            if (globalPrivacySettings != null) {
                this.archiveChats = globalPrivacySettings.archive_and_mute_new_noncontact_peers;
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            this.listAdapter.notifyItemChanged(this.blockedRow);
        } else if (i != NotificationCenter.didSetOrRemoveTwoStepPassword) {
        } else {
            if (objArr.length > 0) {
                this.currentPassword = objArr[0];
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
        this.groupsDetailRow = -1;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.voicesRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.privacyShadowRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.securitySectionRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.passcodeRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.passwordRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.sessionsRow = i13;
        this.rowCount = i14 + 1;
        this.sessionsDetailRow = i14;
        if (getMessagesController().autoarchiveAvailable || getUserConfig().isPremium()) {
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.newChatsHeaderRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.newChatsRow = i16;
            this.rowCount = i17 + 1;
            this.newChatsSectionRow = i17;
        } else {
            this.newChatsHeaderRow = -1;
            this.newChatsRow = -1;
            this.newChatsSectionRow = -1;
        }
        int i18 = this.rowCount;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.advancedSectionRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.deleteAccountRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.deleteAccountDetailRow = i20;
        this.rowCount = i21 + 1;
        this.botsSectionRow = i21;
        if (getUserConfig().hasSecureData) {
            int i22 = this.rowCount;
            this.rowCount = i22 + 1;
            this.passportRow = i22;
        } else {
            this.passportRow = -1;
        }
        int i23 = this.rowCount;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.paymentsClearRow = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.webSessionsRow = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.botsDetailRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactsSectionRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.contactsDeleteRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.contactsSyncRow = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.contactsSuggestRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.contactsDetailRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.secretSectionRow = i31;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.secretMapRow = i32;
        int i34 = i33 + 1;
        this.rowCount = i34;
        this.secretWebpageRow = i33;
        this.rowCount = i34 + 1;
        this.secretDetailRow = i34;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        getConnectionsManager().sendRequest(new TLRPC$TL_account_getPassword(), new PrivacySettingsActivity$$ExternalSyntheticLambda11(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordSettings$17(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new PrivacySettingsActivity$$ExternalSyntheticLambda8(this, (TLRPC$TL_account_password) tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordSettings$16(TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentPassword = tLRPC$TL_account_password;
        TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
        if (getUserConfig().hasSecureData || !tLRPC$TL_account_password.has_secure_values) {
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

    public static String formatRulesString(AccountInstance accountInstance, int i) {
        int i2 = i;
        ArrayList<TLRPC$PrivacyRule> privacyRules = accountInstance.getContactsController().getPrivacyRules(i2);
        if (privacyRules != null && privacyRules.size() != 0) {
            char c = 65535;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < privacyRules.size(); i5++) {
                TLRPC$PrivacyRule tLRPC$PrivacyRule = privacyRules.get(i5);
                if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowChatParticipants) {
                    TLRPC$TL_privacyValueAllowChatParticipants tLRPC$TL_privacyValueAllowChatParticipants = (TLRPC$TL_privacyValueAllowChatParticipants) tLRPC$PrivacyRule;
                    int size = tLRPC$TL_privacyValueAllowChatParticipants.chats.size();
                    for (int i6 = 0; i6 < size; i6++) {
                        TLRPC$Chat chat = accountInstance.getMessagesController().getChat(tLRPC$TL_privacyValueAllowChatParticipants.chats.get(i6));
                        if (chat != null) {
                            i4 += chat.participants_count;
                        }
                    }
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowChatParticipants) {
                    TLRPC$TL_privacyValueDisallowChatParticipants tLRPC$TL_privacyValueDisallowChatParticipants = (TLRPC$TL_privacyValueDisallowChatParticipants) tLRPC$PrivacyRule;
                    int size2 = tLRPC$TL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i7 = 0; i7 < size2; i7++) {
                        TLRPC$Chat chat2 = accountInstance.getMessagesController().getChat(tLRPC$TL_privacyValueDisallowChatParticipants.chats.get(i7));
                        if (chat2 != null) {
                            i3 += chat2.participants_count;
                        }
                    }
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowUsers) {
                    i4 += ((TLRPC$TL_privacyValueAllowUsers) tLRPC$PrivacyRule).users.size();
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowUsers) {
                    i3 += ((TLRPC$TL_privacyValueDisallowUsers) tLRPC$PrivacyRule).users.size();
                } else if (c == 65535) {
                    if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowAll) {
                        c = 0;
                    } else {
                        c = tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowAll ? (char) 1 : 2;
                    }
                }
            }
            if (c == 0 || (c == 65535 && i3 > 0)) {
                if (i2 == 3) {
                    if (i3 == 0) {
                        return LocaleController.getString("P2PEverybody", R.string.P2PEverybody);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", R.string.P2PEverybodyMinus, Integer.valueOf(i3));
                } else if (i3 == 0) {
                    return LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", R.string.LastSeenEverybodyMinus, Integer.valueOf(i3));
                }
            } else if (c == 2 || (c == 65535 && i3 > 0 && i4 > 0)) {
                if (i2 == 3) {
                    if (i4 == 0 && i3 == 0) {
                        return LocaleController.getString("P2PContacts", R.string.P2PContacts);
                    }
                    if (i4 != 0 && i3 != 0) {
                        return LocaleController.formatString("P2PContactsMinusPlus", R.string.P2PContactsMinusPlus, Integer.valueOf(i3), Integer.valueOf(i4));
                    } else if (i3 != 0) {
                        return LocaleController.formatString("P2PContactsMinus", R.string.P2PContactsMinus, Integer.valueOf(i3));
                    } else {
                        return LocaleController.formatString("P2PContactsPlus", R.string.P2PContactsPlus, Integer.valueOf(i4));
                    }
                } else if (i4 == 0 && i3 == 0) {
                    return LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
                } else {
                    if (i4 != 0 && i3 != 0) {
                        return LocaleController.formatString("LastSeenContactsMinusPlus", R.string.LastSeenContactsMinusPlus, Integer.valueOf(i3), Integer.valueOf(i4));
                    } else if (i3 != 0) {
                        return LocaleController.formatString("LastSeenContactsMinus", R.string.LastSeenContactsMinus, Integer.valueOf(i3));
                    } else {
                        return LocaleController.formatString("LastSeenContactsPlus", R.string.LastSeenContactsPlus, Integer.valueOf(i4));
                    }
                }
            } else if (c != 1 && i4 <= 0) {
                return "unknown";
            } else {
                if (i2 == 3) {
                    if (i4 == 0) {
                        return LocaleController.getString("P2PNobody", R.string.P2PNobody);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", R.string.P2PNobodyPlus, Integer.valueOf(i4));
                } else if (i4 == 0) {
                    return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", R.string.LastSeenNobodyPlus, Integer.valueOf(i4));
                }
            }
        } else if (i2 == 3) {
            return LocaleController.getString("P2PNobody", R.string.P2PNobody);
        } else {
            return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == PrivacySettingsActivity.this.passcodeRow || adapterPosition == PrivacySettingsActivity.this.passwordRow || adapterPosition == PrivacySettingsActivity.this.blockedRow || adapterPosition == PrivacySettingsActivity.this.sessionsRow || adapterPosition == PrivacySettingsActivity.this.secretWebpageRow || adapterPosition == PrivacySettingsActivity.this.webSessionsRow || ((adapterPosition == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(1)) || ((adapterPosition == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(0)) || ((adapterPosition == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(2)) || ((adapterPosition == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(4)) || ((adapterPosition == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(5)) || ((adapterPosition == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(6)) || ((adapterPosition == PrivacySettingsActivity.this.voicesRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(8)) || ((adapterPosition == PrivacySettingsActivity.this.deleteAccountRow && !PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) || ((adapterPosition == PrivacySettingsActivity.this.newChatsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingGlobalSettings()) || adapterPosition == PrivacySettingsActivity.this.paymentsClearRow || adapterPosition == PrivacySettingsActivity.this.secretMapRow || adapterPosition == PrivacySettingsActivity.this.contactsSyncRow || adapterPosition == PrivacySettingsActivity.this.passportRow || adapterPosition == PrivacySettingsActivity.this.contactsDeleteRow || adapterPosition == PrivacySettingsActivity.this.contactsSuggestRow)))))))))) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 4) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new ShadowSectionCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String formatPluralString;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            if (itemViewType == 0) {
                String str2 = null;
                int i2 = 16;
                boolean z3 = viewHolder.itemView.getTag() != null && ((Integer) viewHolder.itemView.getTag()).intValue() == i;
                viewHolder.itemView.setTag(Integer.valueOf(i));
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.blockedRow) {
                    int i3 = PrivacySettingsActivity.this.getMessagesController().totalBlockedCount;
                    if (i3 == 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("BlockedEmpty", R.string.BlockedEmpty), true);
                    } else if (i3 > 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), String.format("%d", new Object[]{Integer.valueOf(i3)}), true);
                    } else {
                        textSettingsCell.setText(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), true);
                        z = true;
                    }
                } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("SessionsTitle", R.string.SessionsTitle), false);
                } else if (i == PrivacySettingsActivity.this.webSessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), false);
                } else if (i == PrivacySettingsActivity.this.passwordRow) {
                    if (PrivacySettingsActivity.this.currentPassword == null) {
                        z = true;
                    } else if (PrivacySettingsActivity.this.currentPassword.has_password) {
                        str2 = LocaleController.getString("PasswordOn", R.string.PasswordOn);
                    } else {
                        str2 = LocaleController.getString("PasswordOff", R.string.PasswordOff);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), str2, true);
                } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                    textSettingsCell.setText(LocaleController.getString("Passcode", R.string.Passcode), true);
                } else if (i == PrivacySettingsActivity.this.phoneNumberRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(6)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), str2, true);
                } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(0)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), str2, true);
                } else if (i == PrivacySettingsActivity.this.groupsRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(1)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), str2, true);
                } else if (i == PrivacySettingsActivity.this.callsRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(2)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("Calls", R.string.Calls), str2, true);
                } else if (i == PrivacySettingsActivity.this.profilePhotoRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(4)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), str2, true);
                } else if (i == PrivacySettingsActivity.this.forwardsRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(5)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), str2, true);
                } else {
                    if (i == PrivacySettingsActivity.this.voicesRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(8)) {
                            i2 = 30;
                        } else {
                            if (!PrivacySettingsActivity.this.getUserConfig().isPremium()) {
                                str2 = LocaleController.getString(R.string.P2PEverybody);
                            } else {
                                str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 8);
                            }
                            z2 = false;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.PrivacyVoiceMessages), str2, false);
                        ImageView valueImageView = textSettingsCell.getValueImageView();
                        if (!PrivacySettingsActivity.this.getUserConfig().isPremium()) {
                            valueImageView.setVisibility(0);
                            valueImageView.setImageResource(R.drawable.msg_mini_premiumlock);
                            valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteValueText"), PorterDuff.Mode.MULTIPLY));
                        } else {
                            valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
                        }
                    } else if (i == PrivacySettingsActivity.this.passportRow) {
                        textSettingsCell.setText(LocaleController.getString("TelegramPassport", R.string.TelegramPassport), true);
                    } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (!PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                            int deleteAccountTTL = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                            if (deleteAccountTTL <= 182) {
                                formatPluralString = LocaleController.formatPluralString("Months", deleteAccountTTL / 30, new Object[0]);
                            } else if (deleteAccountTTL == 365) {
                                formatPluralString = LocaleController.formatPluralString("Years", deleteAccountTTL / 365, new Object[0]);
                            } else {
                                formatPluralString = LocaleController.formatPluralString("Days", deleteAccountTTL, new Object[0]);
                            }
                            z2 = false;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor3", R.string.DeleteAccountIfAwayFor3), str2, false);
                    } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), true);
                    } else if (i == PrivacySettingsActivity.this.secretMapRow) {
                        int i4 = SharedConfig.mapPreviewType;
                        if (i4 == 0) {
                            str = LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram);
                        } else if (i4 == 1) {
                            str = LocaleController.getString("MapPreviewProviderGoogle", R.string.MapPreviewProviderGoogle);
                        } else if (i4 != 2) {
                            str = LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex);
                        } else {
                            str = LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), str, true);
                    } else if (i == PrivacySettingsActivity.this.contactsDeleteRow) {
                        textSettingsCell.setText(LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), true);
                    }
                    z = z2;
                }
                textSettingsCell.setDrawLoading(z, i2, z3);
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("DeleteAccountHelp", R.string.DeleteAccountHelp));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", R.string.GroupsAndChannelsHelp));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SessionsInfo", R.string.SessionsInfo));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.secretDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SecretWebPageInfo", R.string.SecretWebPageInfo));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.botsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("PrivacyBotsInfo", R.string.PrivacyBotsInfo));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.contactsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SuggestContactsInfo", R.string.SuggestContactsInfo));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.newChatsSectionRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("ArchiveAndMuteInfo", R.string.ArchiveAndMuteInfo));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.privacySectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                    headerCell.setText(LocaleController.getString("SecurityTitle", R.string.SecurityTitle));
                } else if (i == PrivacySettingsActivity.this.advancedSectionRow) {
                    headerCell.setText(LocaleController.getString("DeleteMyAccount", R.string.DeleteMyAccount));
                } else if (i == PrivacySettingsActivity.this.secretSectionRow) {
                    headerCell.setText(LocaleController.getString("SecretChat", R.string.SecretChat));
                } else if (i == PrivacySettingsActivity.this.botsSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyBots", R.string.PrivacyBots));
                } else if (i == PrivacySettingsActivity.this.contactsSectionRow) {
                    headerCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
                } else if (i == PrivacySettingsActivity.this.newChatsHeaderRow) {
                    headerCell.setText(LocaleController.getString("NewChatsFromNonContacts", R.string.NewChatsFromNonContacts));
                }
            } else if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                    String string = LocaleController.getString("SecretWebPage", R.string.SecretWebPage);
                    if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                        z2 = false;
                    }
                    textCheckCell.setTextAndCheck(string, z2, false);
                } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", R.string.SyncContacts), PrivacySettingsActivity.this.newSync, true);
                } else if (i == PrivacySettingsActivity.this.contactsSuggestRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", R.string.SuggestContacts), PrivacySettingsActivity.this.newSuggest, false);
                } else if (i == PrivacySettingsActivity.this.newChatsRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), PrivacySettingsActivity.this.archiveChats, false);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == PrivacySettingsActivity.this.passportRow || i == PrivacySettingsActivity.this.lastSeenRow || i == PrivacySettingsActivity.this.phoneNumberRow || i == PrivacySettingsActivity.this.blockedRow || i == PrivacySettingsActivity.this.deleteAccountRow || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.webSessionsRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.groupsRow || i == PrivacySettingsActivity.this.paymentsClearRow || i == PrivacySettingsActivity.this.secretMapRow || i == PrivacySettingsActivity.this.contactsDeleteRow) {
                return 0;
            }
            if (i == PrivacySettingsActivity.this.deleteAccountDetailRow || i == PrivacySettingsActivity.this.groupsDetailRow || i == PrivacySettingsActivity.this.sessionsDetailRow || i == PrivacySettingsActivity.this.secretDetailRow || i == PrivacySettingsActivity.this.botsDetailRow || i == PrivacySettingsActivity.this.contactsDetailRow || i == PrivacySettingsActivity.this.newChatsSectionRow) {
                return 1;
            }
            if (i == PrivacySettingsActivity.this.securitySectionRow || i == PrivacySettingsActivity.this.advancedSectionRow || i == PrivacySettingsActivity.this.privacySectionRow || i == PrivacySettingsActivity.this.secretSectionRow || i == PrivacySettingsActivity.this.botsSectionRow || i == PrivacySettingsActivity.this.contactsSectionRow || i == PrivacySettingsActivity.this.newChatsHeaderRow) {
                return 2;
            }
            if (i == PrivacySettingsActivity.this.secretWebpageRow || i == PrivacySettingsActivity.this.contactsSyncRow || i == PrivacySettingsActivity.this.contactsSuggestRow || i == PrivacySettingsActivity.this.newChatsRow) {
                return 3;
            }
            if (i == PrivacySettingsActivity.this.privacyShadowRow) {
                return 4;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        return arrayList;
    }
}
