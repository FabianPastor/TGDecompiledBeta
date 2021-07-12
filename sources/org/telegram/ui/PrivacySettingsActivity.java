package org.telegram.ui;

import android.app.Dialog;
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
import org.telegram.tgnet.RequestDelegate;
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

    static /* synthetic */ void lambda$createView$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onFragmentDestroy$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", NUM), 0).show();
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
            getConnectionsManager().sendRequest(tLRPC$TL_contacts_toggleTopPeers, $$Lambda$PrivacySettingsActivity$zN22sIywlN32WjYbAygRQjKeWOU.INSTANCE);
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
            getConnectionsManager().sendRequest(tLRPC$TL_account_setGlobalPrivacySettings, $$Lambda$PrivacySettingsActivity$P4iel4im6NgAU3dhIROtIKer3qY.INSTANCE);
        }
        if (z3) {
            getUserConfig().saveConfig(false);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", NUM));
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
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PrivacySettingsActivity.this.lambda$createView$15$PrivacySettingsActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$15 */
    public /* synthetic */ void lambda$createView$15$PrivacySettingsActivity(View view, int i) {
        String str;
        View view2 = view;
        int i2 = i;
        if (view.isEnabled()) {
            if (i2 == this.blockedRow) {
                presentFragment(new PrivacyUsersActivity());
                return;
            }
            boolean z = false;
            if (i2 == this.sessionsRow) {
                presentFragment(new SessionsActivity(0));
            } else if (i2 == this.webSessionsRow) {
                presentFragment(new SessionsActivity(1));
            } else {
                int i3 = 6;
                if (i2 == this.deleteAccountRow) {
                    if (getParentActivity() != null) {
                        int deleteAccountTTL = getContactsController().getDeleteAccountTTL();
                        int i4 = deleteAccountTTL <= 31 ? 0 : deleteAccountTTL <= 93 ? 1 : deleteAccountTTL <= 182 ? 2 : 3;
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("DeleteAccountTitle", NUM));
                        String[] strArr = {LocaleController.formatPluralString("Months", 1), LocaleController.formatPluralString("Months", 3), LocaleController.formatPluralString("Months", 6), LocaleController.formatPluralString("Years", 1)};
                        LinearLayout linearLayout = new LinearLayout(getParentActivity());
                        linearLayout.setOrientation(1);
                        builder.setView(linearLayout);
                        int i5 = 0;
                        while (i5 < 4) {
                            RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            radioColorCell.setTag(Integer.valueOf(i5));
                            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                            radioColorCell.setTextAndValue(strArr[i5], i4 == i5);
                            linearLayout.addView(radioColorCell);
                            radioColorCell.setOnClickListener(new View.OnClickListener(builder) {
                                public final /* synthetic */ AlertDialog.Builder f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(View view) {
                                    PrivacySettingsActivity.this.lambda$createView$4$PrivacySettingsActivity(this.f$1, view);
                                }
                            });
                            i5++;
                        }
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
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
                    TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                    if (tLRPC$TL_account_password != null) {
                        if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                        }
                        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                        if (tLRPC$TL_account_password2.has_password) {
                            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                            twoStepVerificationActivity.setPassword(this.currentPassword);
                            presentFragment(twoStepVerificationActivity);
                            return;
                        }
                        if (!TextUtils.isEmpty(tLRPC$TL_account_password2.email_unconfirmed_pattern)) {
                            i3 = 5;
                        }
                        presentFragment(new TwoStepVerificationSetupActivity(i3, this.currentPassword));
                    }
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
                        TextCheckCell textCheckCell = (TextCheckCell) view2;
                        if (getMessagesController().secretWebpagePreview == 1) {
                            z = true;
                        }
                        textCheckCell.setChecked(z);
                    }
                } else if (i2 == this.contactsDeleteRow) {
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                        builder2.setTitle(LocaleController.getString("SyncContactsDeleteTitle", NUM));
                        builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", NUM)));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                PrivacySettingsActivity.this.lambda$createView$6$PrivacySettingsActivity(dialogInterface, i);
                            }
                        });
                        AlertDialog create = builder2.create();
                        showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                } else if (i2 == this.contactsSuggestRow) {
                    TextCheckCell textCheckCell2 = (TextCheckCell) view2;
                    boolean z2 = this.newSuggest;
                    if (z2) {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
                        builder3.setTitle(LocaleController.getString("SuggestContactsTitle", NUM));
                        builder3.setMessage(LocaleController.getString("SuggestContactsAlert", NUM));
                        builder3.setPositiveButton(LocaleController.getString("MuteDisable", NUM), new DialogInterface.OnClickListener(textCheckCell2) {
                            public final /* synthetic */ TextCheckCell f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                PrivacySettingsActivity.this.lambda$createView$9$PrivacySettingsActivity(this.f$1, dialogInterface, i);
                            }
                        });
                        builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog create2 = builder3.create();
                        showDialog(create2);
                        TextView textView2 = (TextView) create2.getButton(-1);
                        if (textView2 != null) {
                            textView2.setTextColor(Theme.getColor("dialogTextRed2"));
                            return;
                        }
                        return;
                    }
                    boolean z3 = !z2;
                    this.newSuggest = z3;
                    textCheckCell2.setChecked(z3);
                } else if (i2 == this.newChatsRow) {
                    boolean z4 = !this.archiveChats;
                    this.archiveChats = z4;
                    ((TextCheckCell) view2).setChecked(z4);
                } else if (i2 == this.contactsSyncRow) {
                    boolean z5 = !this.newSync;
                    this.newSync = z5;
                    if (view2 instanceof TextCheckCell) {
                        ((TextCheckCell) view2).setChecked(z5);
                    }
                } else if (i2 == this.secretMapRow) {
                    AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new Runnable() {
                        public final void run() {
                            PrivacySettingsActivity.this.lambda$createView$10$PrivacySettingsActivity();
                        }
                    }, false);
                } else if (i2 == this.paymentsClearRow) {
                    AlertDialog.Builder builder4 = new AlertDialog.Builder((Context) getParentActivity());
                    builder4.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
                    builder4.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", NUM));
                    LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                    linearLayout2.setOrientation(1);
                    builder4.setView(linearLayout2);
                    int i6 = 0;
                    for (int i7 = 2; i6 < i7; i7 = 2) {
                        if (i6 == 0) {
                            str = LocaleController.getString("PrivacyClearShipping", NUM);
                        } else {
                            str = i6 == 1 ? LocaleController.getString("PrivacyClearPayment", NUM) : null;
                        }
                        this.clear[i6] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                        checkBoxCell.setTag(Integer.valueOf(i6));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        linearLayout2.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(str, (String) null, true, false);
                        checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                        checkBoxCell.setOnClickListener(new View.OnClickListener() {
                            public final void onClick(View view) {
                                PrivacySettingsActivity.this.lambda$createView$11$PrivacySettingsActivity(view);
                            }
                        });
                        i6++;
                    }
                    builder4.setPositiveButton(LocaleController.getString("ClearButton", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PrivacySettingsActivity.this.lambda$createView$14$PrivacySettingsActivity(dialogInterface, i);
                        }
                    });
                    builder4.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder4.create());
                    AlertDialog create3 = builder4.create();
                    showDialog(create3);
                    TextView textView3 = (TextView) create3.getButton(-1);
                    if (textView3 != null) {
                        textView3.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (i2 == this.passportRow) {
                    presentFragment(new PassportActivity(5, 0, "", "", (String) null, (String) null, (String) null, (TLRPC$TL_account_authorizationForm) null, (TLRPC$TL_account_password) null));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$PrivacySettingsActivity(AlertDialog.Builder builder, View view) {
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
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL = new TLRPC$TL_account_setAccountTTL();
        TLRPC$TL_accountDaysTTL tLRPC$TL_accountDaysTTL = new TLRPC$TL_accountDaysTTL();
        tLRPC$TL_account_setAccountTTL.ttl = tLRPC$TL_accountDaysTTL;
        tLRPC$TL_accountDaysTTL.days = i;
        getConnectionsManager().sendRequest(tLRPC$TL_account_setAccountTTL, new RequestDelegate(alertDialog, tLRPC$TL_account_setAccountTTL) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_account_setAccountTTL f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PrivacySettingsActivity.this.lambda$createView$3$PrivacySettingsActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$PrivacySettingsActivity(AlertDialog alertDialog, TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_account_setAccountTTL) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_account_setAccountTTL f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$2$PrivacySettingsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$PrivacySettingsActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL) {
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
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        AlertDialog show = new AlertDialog.Builder(getParentActivity(), 3).show();
        this.progressDialog = show;
        show.setCanCacnel(false);
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z = this.newSync;
            userConfig.syncContacts = z;
            this.currentSync = z;
            getUserConfig().saveConfig(false);
        }
        getContactsController().deleteAllContacts(new Runnable() {
            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$5$PrivacySettingsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$PrivacySettingsActivity() {
        this.progressDialog.dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$PrivacySettingsActivity(TextCheckCell textCheckCell, DialogInterface dialogInterface, int i) {
        TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tLRPC$TL_payments_clearSavedInfo.credentials = zArr[1];
        tLRPC$TL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tLRPC$TL_payments_clearSavedInfo, new RequestDelegate(textCheckCell) {
            public final /* synthetic */ TextCheckCell f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PrivacySettingsActivity.this.lambda$createView$8$PrivacySettingsActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$PrivacySettingsActivity(TextCheckCell textCheckCell, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(textCheckCell) {
            public final /* synthetic */ TextCheckCell f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$7$PrivacySettingsActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$PrivacySettingsActivity(TextCheckCell textCheckCell) {
        boolean z = !this.newSuggest;
        this.newSuggest = z;
        textCheckCell.setChecked(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$PrivacySettingsActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ void lambda$createView$11$PrivacySettingsActivity(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[intValue] = !zArr[intValue];
        checkBoxCell.setChecked(zArr[intValue], true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$14 */
    public /* synthetic */ void lambda$createView$14$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", NUM));
        builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacySettingsActivity.this.lambda$createView$13$PrivacySettingsActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$13 */
    public /* synthetic */ void lambda$createView$13$PrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        String str;
        TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tLRPC$TL_payments_clearSavedInfo.credentials = zArr[1];
        tLRPC$TL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tLRPC$TL_payments_clearSavedInfo, $$Lambda$PrivacySettingsActivity$VoCFnz7obFsdamd0LFBp6x0tQ7g.INSTANCE);
        boolean[] zArr2 = this.clear;
        if (zArr2[0] && zArr2[1]) {
            str = LocaleController.getString("PrivacyPaymentsPaymentShippingCleared", NUM);
        } else if (zArr2[0]) {
            str = LocaleController.getString("PrivacyPaymentsShippingInfoCleared", NUM);
        } else if (zArr2[1]) {
            str = LocaleController.getString("PrivacyPaymentsPaymentInfoCleared", NUM);
        } else {
            return;
        }
        BulletinFactory.of((BaseFragment) this).createSimpleBulletin(NUM, str).show();
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
        if (getMessagesController().autoarchiveAvailable) {
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
        getConnectionsManager().sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PrivacySettingsActivity.this.lambda$loadPasswordSettings$17$PrivacySettingsActivity(tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordSettings$17 */
    public /* synthetic */ void lambda$loadPasswordSettings$17$PrivacySettingsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_account_password) tLObject) {
                public final /* synthetic */ TLRPC$TL_account_password f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PrivacySettingsActivity.this.lambda$loadPasswordSettings$16$PrivacySettingsActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordSettings$16 */
    public /* synthetic */ void lambda$loadPasswordSettings$16$PrivacySettingsActivity(TLRPC$TL_account_password tLRPC$TL_account_password) {
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
        if (privacyRules.size() != 0) {
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
                        return LocaleController.getString("P2PEverybody", NUM);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", NUM, Integer.valueOf(i3));
                } else if (i3 == 0) {
                    return LocaleController.getString("LastSeenEverybody", NUM);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", NUM, Integer.valueOf(i3));
                }
            } else if (c == 2 || (c == 65535 && i3 > 0 && i4 > 0)) {
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
            } else if (c != 1 && i4 <= 0) {
                return "unknown";
            } else {
                if (i2 == 3) {
                    if (i4 == 0) {
                        return LocaleController.getString("P2PNobody", NUM);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", NUM, Integer.valueOf(i4));
                } else if (i4 == 0) {
                    return LocaleController.getString("LastSeenNobody", NUM);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", NUM, Integer.valueOf(i4));
                }
            }
        } else if (i2 == 3) {
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == PrivacySettingsActivity.this.passcodeRow || adapterPosition == PrivacySettingsActivity.this.passwordRow || adapterPosition == PrivacySettingsActivity.this.blockedRow || adapterPosition == PrivacySettingsActivity.this.sessionsRow || adapterPosition == PrivacySettingsActivity.this.secretWebpageRow || adapterPosition == PrivacySettingsActivity.this.webSessionsRow || ((adapterPosition == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) || ((adapterPosition == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) || ((adapterPosition == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) || ((adapterPosition == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) || ((adapterPosition == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) || ((adapterPosition == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) || ((adapterPosition == PrivacySettingsActivity.this.deleteAccountRow && !PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) || ((adapterPosition == PrivacySettingsActivity.this.newChatsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingGlobalSettings()) || adapterPosition == PrivacySettingsActivity.this.paymentsClearRow || adapterPosition == PrivacySettingsActivity.this.secretMapRow || adapterPosition == PrivacySettingsActivity.this.contactsSyncRow || adapterPosition == PrivacySettingsActivity.this.passportRow || adapterPosition == PrivacySettingsActivity.this.contactsDeleteRow || adapterPosition == PrivacySettingsActivity.this.contactsSuggestRow))))))))) {
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
            } else if (i != 2) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
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
                        textSettingsCell.setTextAndValue(LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("BlockedEmpty", NUM), true);
                    } else if (i3 > 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BlockedUsers", NUM), String.format("%d", new Object[]{Integer.valueOf(i3)}), true);
                    } else {
                        textSettingsCell.setText(LocaleController.getString("BlockedUsers", NUM), true);
                        z = true;
                    }
                } else if (i == PrivacySettingsActivity.this.sessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("SessionsTitle", NUM), false);
                } else if (i == PrivacySettingsActivity.this.webSessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("WebSessionsTitle", NUM), false);
                } else if (i == PrivacySettingsActivity.this.passwordRow) {
                    if (PrivacySettingsActivity.this.currentPassword == null) {
                        z = true;
                    } else if (PrivacySettingsActivity.this.currentPassword.has_password) {
                        str2 = LocaleController.getString("PasswordOn", NUM);
                    } else {
                        str2 = LocaleController.getString("PasswordOff", NUM);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("TwoStepVerification", NUM), str2, true);
                } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                    textSettingsCell.setText(LocaleController.getString("Passcode", NUM), true);
                } else if (i == PrivacySettingsActivity.this.phoneNumberRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyPhone", NUM), str2, true);
                } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                    if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) {
                        z = true;
                        i2 = 30;
                    } else {
                        str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", NUM), str2, true);
                } else {
                    if (i == PrivacySettingsActivity.this.groupsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) {
                            i2 = 30;
                        } else {
                            str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                            z2 = false;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", NUM), str2, false);
                    } else if (i == PrivacySettingsActivity.this.callsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) {
                            z = true;
                            i2 = 30;
                        } else {
                            str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Calls", NUM), str2, true);
                    } else if (i == PrivacySettingsActivity.this.profilePhotoRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) {
                            z = true;
                            i2 = 30;
                        } else {
                            str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", NUM), str2, true);
                    } else if (i == PrivacySettingsActivity.this.forwardsRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) {
                            z = true;
                            i2 = 30;
                        } else {
                            str2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PrivacyForwards", NUM), str2, true);
                    } else if (i == PrivacySettingsActivity.this.passportRow) {
                        textSettingsCell.setText(LocaleController.getString("TelegramPassport", NUM), true);
                    } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                        if (!PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                            int deleteAccountTTL = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                            if (deleteAccountTTL <= 182) {
                                str2 = LocaleController.formatPluralString("Months", deleteAccountTTL / 30);
                            } else if (deleteAccountTTL == 365) {
                                str2 = LocaleController.formatPluralString("Years", deleteAccountTTL / 365);
                            } else {
                                str2 = LocaleController.formatPluralString("Days", deleteAccountTTL);
                            }
                            z2 = false;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor3", NUM), str2, false);
                    } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyPaymentsClear", NUM), true);
                    } else if (i == PrivacySettingsActivity.this.secretMapRow) {
                        int i4 = SharedConfig.mapPreviewType;
                        if (i4 == 0) {
                            str = LocaleController.getString("MapPreviewProviderTelegram", NUM);
                        } else if (i4 == 1) {
                            str = LocaleController.getString("MapPreviewProviderGoogle", NUM);
                        } else if (i4 != 2) {
                            str = LocaleController.getString("MapPreviewProviderYandex", NUM);
                        } else {
                            str = LocaleController.getString("MapPreviewProviderNobody", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", NUM), str, true);
                    } else if (i == PrivacySettingsActivity.this.contactsDeleteRow) {
                        textSettingsCell.setText(LocaleController.getString("SyncContactsDelete", NUM), true);
                    }
                    z = z2;
                }
                textSettingsCell.setDrawLoading(z, i2, z3);
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("DeleteAccountHelp", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SessionsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.secretDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SecretWebPageInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.botsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("PrivacyBotsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.contactsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SuggestContactsInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PrivacySettingsActivity.this.newChatsSectionRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("ArchiveAndMuteInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.privacySectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyTitle", NUM));
                } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                    headerCell.setText(LocaleController.getString("SecurityTitle", NUM));
                } else if (i == PrivacySettingsActivity.this.advancedSectionRow) {
                    headerCell.setText(LocaleController.getString("DeleteMyAccount", NUM));
                } else if (i == PrivacySettingsActivity.this.secretSectionRow) {
                    headerCell.setText(LocaleController.getString("SecretChat", NUM));
                } else if (i == PrivacySettingsActivity.this.botsSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyBots", NUM));
                } else if (i == PrivacySettingsActivity.this.contactsSectionRow) {
                    headerCell.setText(LocaleController.getString("Contacts", NUM));
                } else if (i == PrivacySettingsActivity.this.newChatsHeaderRow) {
                    headerCell.setText(LocaleController.getString("NewChatsFromNonContacts", NUM));
                }
            } else if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                    String string = LocaleController.getString("SecretWebPage", NUM);
                    if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                        z2 = false;
                    }
                    textCheckCell.setTextAndCheck(string, z2, false);
                } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", NUM), PrivacySettingsActivity.this.newSync, true);
                } else if (i == PrivacySettingsActivity.this.contactsSuggestRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", NUM), PrivacySettingsActivity.this.newSuggest, false);
                } else if (i == PrivacySettingsActivity.this.newChatsRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveAndMute", NUM), PrivacySettingsActivity.this.archiveChats, false);
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
