package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_account_authorizations;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_getWebAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC$TL_account_resetWebAuthorization;
import org.telegram.tgnet.TLRPC$TL_account_resetWebAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_setAuthorizationTTL;
import org.telegram.tgnet.TLRPC$TL_account_webAuthorizations;
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.SessionBottomSheet;

public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public TLRPC$TL_authorization currentSession;
    /* access modifiers changed from: private */
    public int currentSessionRow;
    /* access modifiers changed from: private */
    public int currentSessionSectionRow;
    /* access modifiers changed from: private */
    public int currentType;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public FlickerLoadingView globalFlickerLoadingView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    /* access modifiers changed from: private */
    public int noOtherSessionsRow;
    /* access modifiers changed from: private */
    public int otherSessionsEndRow;
    /* access modifiers changed from: private */
    public int otherSessionsSectionRow;
    /* access modifiers changed from: private */
    public int otherSessionsStartRow;
    /* access modifiers changed from: private */
    public int otherSessionsTerminateDetail;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> passwordSessions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int passwordSessionsDetailRow;
    /* access modifiers changed from: private */
    public int passwordSessionsEndRow;
    /* access modifiers changed from: private */
    public int passwordSessionsSectionRow;
    /* access modifiers changed from: private */
    public int passwordSessionsStartRow;
    /* access modifiers changed from: private */
    public int qrCodeDividerRow;
    /* access modifiers changed from: private */
    public int qrCodeRow;
    /* access modifiers changed from: private */
    public int repeatLoad = 0;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> sessions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int terminateAllSessionsDetailRow;
    /* access modifiers changed from: private */
    public int terminateAllSessionsRow;
    /* access modifiers changed from: private */
    public int ttlDays;
    /* access modifiers changed from: private */
    public int ttlDivideRow;
    /* access modifiers changed from: private */
    public int ttlHeaderRow;
    /* access modifiers changed from: private */
    public int ttlRow;
    /* access modifiers changed from: private */
    public UndoView undoView;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public SessionsActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        lambda$loadSessions$17(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public View createView(Context context) {
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalFlickerLoadingView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("Devices", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    SessionsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this, context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(150);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        defaultItemAnimator.setMoveInterpolator(cubicBezierInterpolator);
        defaultItemAnimator.setTranslationInterpolator(cubicBezierInterpolator);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SessionsActivity$$ExternalSyntheticLambda20(this));
        if (this.currentType == 0) {
            AnonymousClass3 r1 = new UndoView(context) {
                public void hide(boolean z, int i) {
                    if (!z) {
                        TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) getCurrentInfoObject();
                        TLRPC$TL_account_resetAuthorization tLRPC$TL_account_resetAuthorization = new TLRPC$TL_account_resetAuthorization();
                        tLRPC$TL_account_resetAuthorization.hash = tLRPC$TL_authorization.hash;
                        ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(tLRPC$TL_account_resetAuthorization, new SessionsActivity$3$$ExternalSyntheticLambda1(this, tLRPC$TL_authorization));
                    }
                    super.hide(z, i);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$hide$1(TLRPC$TL_authorization tLRPC$TL_authorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SessionsActivity$3$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLRPC$TL_authorization));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$hide$0(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
                    if (tLRPC$TL_error == null) {
                        SessionsActivity.this.sessions.remove(tLRPC$TL_authorization);
                        SessionsActivity.this.passwordSessions.remove(tLRPC$TL_authorization);
                        SessionsActivity.this.updateRows();
                        if (SessionsActivity.this.listAdapter != null) {
                            SessionsActivity.this.listAdapter.notifyDataSetChanged();
                        }
                        SessionsActivity.this.lambda$loadSessions$17(true);
                    }
                }
            };
            this.undoView = r1;
            frameLayout2.addView(r1, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        updateRows();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(View view, int i) {
        String str;
        String str2;
        TLRPC$TL_authorization tLRPC$TL_authorization;
        String str3;
        int i2 = i;
        boolean z = true;
        if (i2 == this.ttlRow) {
            if (getParentActivity() != null) {
                int i3 = this.ttlDays;
                int i4 = i3 <= 7 ? 0 : i3 <= 93 ? 1 : i3 <= 183 ? 2 : 3;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("SessionsSelfDestruct", NUM));
                String[] strArr = {LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
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
                    radioColorCell.setOnClickListener(new SessionsActivity$$ExternalSyntheticLambda3(this, builder));
                    i5++;
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (i2 == this.terminateAllSessionsRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                if (this.currentType == 0) {
                    builder2.setMessage(LocaleController.getString("AreYouSureSessions", NUM));
                    builder2.setTitle(LocaleController.getString("AreYouSureSessionsTitle", NUM));
                    str3 = LocaleController.getString("Terminate", NUM);
                } else {
                    builder2.setMessage(LocaleController.getString("AreYouSureWebSessions", NUM));
                    builder2.setTitle(LocaleController.getString("TerminateWebSessionsTitle", NUM));
                    str3 = LocaleController.getString("Disconnect", NUM);
                }
                builder2.setPositiveButton(str3, new SessionsActivity$$ExternalSyntheticLambda1(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder2.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (((i2 >= this.otherSessionsStartRow && i2 < this.otherSessionsEndRow) || ((i2 >= this.passwordSessionsStartRow && i2 < this.passwordSessionsEndRow) || i2 == this.currentSessionRow)) && getParentActivity() != null) {
            if (this.currentType == 0) {
                if (i2 == this.currentSessionRow) {
                    tLRPC$TL_authorization = this.currentSession;
                } else {
                    int i6 = this.otherSessionsStartRow;
                    if (i2 < i6 || i2 >= this.otherSessionsEndRow) {
                        tLRPC$TL_authorization = (TLRPC$TL_authorization) this.passwordSessions.get(i2 - this.passwordSessionsStartRow);
                    } else {
                        tLRPC$TL_authorization = (TLRPC$TL_authorization) this.sessions.get(i2 - i6);
                    }
                    z = false;
                }
                showSessionBottomSheet(tLRPC$TL_authorization, z);
                return;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            boolean[] zArr = new boolean[1];
            if (this.currentType == 0) {
                builder3.setMessage(LocaleController.getString("TerminateSessionText", NUM));
                builder3.setTitle(LocaleController.getString("AreYouSureSessionTitle", NUM));
                str = LocaleController.getString("Terminate", NUM);
            } else {
                TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) this.sessions.get(i2 - this.otherSessionsStartRow);
                builder3.setMessage(LocaleController.formatString("TerminateWebSessionText", NUM, tLRPC$TL_webAuthorization.domain));
                builder3.setTitle(LocaleController.getString("TerminateWebSessionTitle", NUM));
                String string = LocaleController.getString("Disconnect", NUM);
                FrameLayout frameLayout = new FrameLayout(getParentActivity());
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_webAuthorization.bot_id));
                if (user != null) {
                    str2 = UserObject.getFirstName(user);
                } else {
                    str2 = "";
                }
                CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCell.setText(LocaleController.formatString("TerminateWebSessionStop", NUM, str2), "", false, false);
                checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                checkBoxCell.setOnClickListener(new SessionsActivity$$ExternalSyntheticLambda4(zArr));
                builder3.setCustomViewOffset(16);
                builder3.setView(frameLayout);
                str = string;
            }
            builder3.setPositiveButton(str, new SessionsActivity$$ExternalSyntheticLambda2(this, i2, zArr));
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create2 = builder3.create();
            showDialog(create2);
            TextView textView2 = (TextView) create2.getButton(-1);
            if (textView2 != null) {
                textView2.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(AlertDialog.Builder builder, View view) {
        int i;
        builder.getDismissRunnable().run();
        Integer num = (Integer) view.getTag();
        if (num.intValue() == 0) {
            i = 7;
        } else if (num.intValue() == 1) {
            i = 90;
        } else if (num.intValue() == 2) {
            i = 183;
        } else {
            i = num.intValue() == 3 ? 365 : 0;
        }
        TLRPC$TL_account_setAuthorizationTTL tLRPC$TL_account_setAuthorizationTTL = new TLRPC$TL_account_setAuthorizationTTL();
        tLRPC$TL_account_setAuthorizationTTL.authorization_ttl_days = i;
        this.ttlDays = i;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_setAuthorizationTTL, SessionsActivity$$ExternalSyntheticLambda19.INSTANCE);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_resetAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda13(this));
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resetWebAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda14(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda5(this, tLRPC$TL_error, tLObject));
        for (int i = 0; i < 4; i++) {
            UserConfig instance = UserConfig.getInstance(i);
            if (instance.isClientActivated()) {
                instance.registeredForPush = false;
                instance.saveConfig(false);
                MessagesController.getInstance(i).registerForPush(SharedConfig.pushType, SharedConfig.pushString);
                ConnectionsManager.getInstance(i).setUserId(instance.getClientUserId());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (getParentActivity() != null && tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
            BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("AllSessionsTerminated", NUM)).show();
            lambda$loadSessions$17(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (getParentActivity() != null) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("UnknownError", NUM)).show();
            } else {
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("AllWebSessionsTerminated", NUM)).show();
            }
            lambda$loadSessions$17(false);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$7(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            zArr[0] = !zArr[0];
            ((CheckBoxCell) view).setChecked(zArr[0], true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(int i, boolean[] zArr, DialogInterface dialogInterface, int i2) {
        TLRPC$TL_authorization tLRPC$TL_authorization;
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
            if (this.currentType == 0) {
                int i3 = this.otherSessionsStartRow;
                if (i < i3 || i >= this.otherSessionsEndRow) {
                    tLRPC$TL_authorization = (TLRPC$TL_authorization) this.passwordSessions.get(i - this.passwordSessionsStartRow);
                } else {
                    tLRPC$TL_authorization = (TLRPC$TL_authorization) this.sessions.get(i - i3);
                }
                TLRPC$TL_account_resetAuthorization tLRPC$TL_account_resetAuthorization = new TLRPC$TL_account_resetAuthorization();
                tLRPC$TL_account_resetAuthorization.hash = tLRPC$TL_authorization.hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_resetAuthorization, new SessionsActivity$$ExternalSyntheticLambda15(this, alertDialog, tLRPC$TL_authorization));
                return;
            }
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
            TLRPC$TL_account_resetWebAuthorization tLRPC$TL_account_resetWebAuthorization = new TLRPC$TL_account_resetWebAuthorization();
            tLRPC$TL_account_resetWebAuthorization.hash = tLRPC$TL_webAuthorization.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_resetWebAuthorization, new SessionsActivity$$ExternalSyntheticLambda16(this, alertDialog, tLRPC$TL_webAuthorization));
            if (zArr[0]) {
                MessagesController.getInstance(this.currentAccount).blockPeer(tLRPC$TL_webAuthorization.bot_id);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(AlertDialog alertDialog, TLRPC$TL_authorization tLRPC$TL_authorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda9(this, alertDialog, tLRPC$TL_error, tLRPC$TL_authorization));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLRPC$TL_error == null) {
            this.sessions.remove(tLRPC$TL_authorization);
            this.passwordSessions.remove(tLRPC$TL_authorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(AlertDialog alertDialog, TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda10(this, alertDialog, tLRPC$TL_error, tLRPC$TL_webAuthorization));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLRPC$TL_error == null) {
            this.sessions.remove(tLRPC$TL_webAuthorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    private void showSessionBottomSheet(TLRPC$TL_authorization tLRPC$TL_authorization, boolean z) {
        if (tLRPC$TL_authorization != null) {
            new SessionBottomSheet(this, tLRPC$TL_authorization, z, new SessionBottomSheet.Callback() {
                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onSessionTerminated$0() {
                }

                public void onSessionTerminated(TLRPC$TL_authorization tLRPC$TL_authorization) {
                    SessionsActivity.this.sessions.remove(tLRPC$TL_authorization);
                    SessionsActivity.this.passwordSessions.remove(tLRPC$TL_authorization);
                    SessionsActivity.this.updateRows();
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    TLRPC$TL_account_resetAuthorization tLRPC$TL_account_resetAuthorization = new TLRPC$TL_account_resetAuthorization();
                    tLRPC$TL_account_resetAuthorization.hash = tLRPC$TL_authorization.hash;
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(tLRPC$TL_account_resetAuthorization, SessionsActivity$4$$ExternalSyntheticLambda1.INSTANCE);
                }
            }).show();
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newSessionReceived) {
            lambda$loadSessions$17(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: loadSessions */
    public void lambda$loadSessions$17(boolean z) {
        if (!this.loading) {
            if (!z) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda18(this, z)), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getWebAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda17(this, z)), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSessions$16(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda7(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSessions$15(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        this.loading = false;
        this.listAdapter.getItemCount();
        if (tLRPC$TL_error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TLRPC$TL_account_authorizations tLRPC$TL_account_authorizations = (TLRPC$TL_account_authorizations) tLObject;
            int size = tLRPC$TL_account_authorizations.authorizations.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_authorization tLRPC$TL_authorization = tLRPC$TL_account_authorizations.authorizations.get(i);
                if ((tLRPC$TL_authorization.flags & 1) != 0) {
                    this.currentSession = tLRPC$TL_authorization;
                } else if (tLRPC$TL_authorization.password_pending) {
                    this.passwordSessions.add(tLRPC$TL_authorization);
                } else {
                    this.sessions.add(tLRPC$TL_authorization);
                }
            }
            this.ttlDays = tLRPC$TL_account_authorizations.authorization_ttl_days;
            updateRows();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        int i2 = this.repeatLoad;
        if (i2 > 0) {
            int i3 = i2 - 1;
            this.repeatLoad = i3;
            if (i3 > 0) {
                AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda11(this, z), 2500);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSessions$19(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda8(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSessions$18(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        this.loading = false;
        if (tLRPC$TL_error == null) {
            this.sessions.clear();
            TLRPC$TL_account_webAuthorizations tLRPC$TL_account_webAuthorizations = (TLRPC$TL_account_webAuthorizations) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_account_webAuthorizations.users, false);
            this.sessions.addAll(tLRPC$TL_account_webAuthorizations.authorizations);
            updateRows();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        int i = this.repeatLoad;
        if (i > 0) {
            int i2 = i - 1;
            this.repeatLoad = i2;
            if (i2 > 0) {
                AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda12(this, z), 2500);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        this.currentSessionSectionRow = -1;
        this.currentSessionRow = -1;
        this.terminateAllSessionsRow = -1;
        this.terminateAllSessionsDetailRow = -1;
        this.passwordSessionsSectionRow = -1;
        this.passwordSessionsStartRow = -1;
        this.passwordSessionsEndRow = -1;
        this.passwordSessionsDetailRow = -1;
        this.otherSessionsSectionRow = -1;
        this.otherSessionsStartRow = -1;
        this.otherSessionsEndRow = -1;
        this.otherSessionsTerminateDetail = -1;
        this.noOtherSessionsRow = -1;
        this.qrCodeRow = -1;
        this.qrCodeDividerRow = -1;
        this.ttlHeaderRow = -1;
        this.ttlRow = -1;
        this.ttlDivideRow = -1;
        if (this.currentType == 0 && getMessagesController().qrLoginCamera) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.qrCodeRow = i;
            this.rowCount = i2 + 1;
            this.qrCodeDividerRow = i2;
        }
        if (!this.loading) {
            if (this.currentSession != null) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.currentSessionSectionRow = i3;
                this.rowCount = i4 + 1;
                this.currentSessionRow = i4;
            }
            if (!this.passwordSessions.isEmpty() || !this.sessions.isEmpty()) {
                int i5 = this.rowCount;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.terminateAllSessionsRow = i5;
                this.rowCount = i6 + 1;
                this.terminateAllSessionsDetailRow = i6;
                this.noOtherSessionsRow = -1;
            } else {
                this.terminateAllSessionsRow = -1;
                this.terminateAllSessionsDetailRow = -1;
                if (this.currentType == 1 || this.currentSession != null) {
                    int i7 = this.rowCount;
                    this.rowCount = i7 + 1;
                    this.noOtherSessionsRow = i7;
                } else {
                    this.noOtherSessionsRow = -1;
                }
            }
            if (!this.passwordSessions.isEmpty()) {
                int i8 = this.rowCount;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.passwordSessionsSectionRow = i8;
                this.passwordSessionsStartRow = i9;
                int size = i9 + this.passwordSessions.size();
                this.rowCount = size;
                this.passwordSessionsEndRow = size;
                this.rowCount = size + 1;
                this.passwordSessionsDetailRow = size;
            }
            if (!this.sessions.isEmpty()) {
                int i10 = this.rowCount;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.otherSessionsSectionRow = i10;
                this.otherSessionsStartRow = i11;
                this.otherSessionsEndRow = i11 + this.sessions.size();
                int size2 = this.rowCount + this.sessions.size();
                this.rowCount = size2;
                this.rowCount = size2 + 1;
                this.otherSessionsTerminateDetail = size2;
            }
            if (this.ttlDays > 0) {
                int i12 = this.rowCount;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.ttlHeaderRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.ttlRow = i13;
                this.rowCount = i14 + 1;
                this.ttlDivideRow = i14;
            }
        } else if (this.currentType == 0) {
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.currentSessionSectionRow = i15;
            this.rowCount = i16 + 1;
            this.currentSessionRow = i16;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == SessionsActivity.this.terminateAllSessionsRow || (adapterPosition >= SessionsActivity.this.otherSessionsStartRow && adapterPosition < SessionsActivity.this.otherSessionsEndRow) || ((adapterPosition >= SessionsActivity.this.passwordSessionsStartRow && adapterPosition < SessionsActivity.this.passwordSessionsEndRow) || adapterPosition == SessionsActivity.this.currentSessionRow || adapterPosition == SessionsActivity.this.ttlRow);
        }

        public int getItemCount() {
            return SessionsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new TextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 5) {
                view = new ScanQRCodeView(this.mContext);
            } else if (i != 6) {
                view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i == SessionsActivity.this.terminateAllSessionsRow) {
                    textCell.setColors("windowBackgroundWhiteRedText2", "windowBackgroundWhiteRedText2");
                    textCell.setTag("windowBackgroundWhiteRedText2");
                    if (SessionsActivity.this.currentType == 0) {
                        textCell.setTextAndIcon(LocaleController.getString("TerminateAllSessions", NUM), NUM, false);
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("TerminateAllWebSessions", NUM), NUM, false);
                    }
                } else if (i == SessionsActivity.this.qrCodeRow) {
                    textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                    textCell.setTag("windowBackgroundWhiteBlueText4");
                    textCell.setTextAndIcon(LocaleController.getString("AuthAnotherClient", NUM), NUM, true ^ SessionsActivity.this.sessions.isEmpty());
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setFixedSize(0);
                if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                    if (SessionsActivity.this.currentType == 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", NUM));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", NUM));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == SessionsActivity.this.otherSessionsTerminateDetail) {
                    if (SessionsActivity.this.currentType != 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", NUM));
                    } else if (SessionsActivity.this.sessions.isEmpty()) {
                        textInfoPrivacyCell.setText("");
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString("SessionsListInfo", NUM));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == SessionsActivity.this.passwordSessionsDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("LoginAttemptsInfo", NUM));
                    if (SessionsActivity.this.otherSessionsTerminateDetail == -1) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    } else {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    }
                } else if (i == SessionsActivity.this.qrCodeDividerRow || i == SessionsActivity.this.ttlDivideRow || i == SessionsActivity.this.noOtherSessionsRow) {
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setFixedSize(12);
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == SessionsActivity.this.currentSessionSectionRow) {
                    headerCell.setText(LocaleController.getString("CurrentSession", NUM));
                } else if (i == SessionsActivity.this.otherSessionsSectionRow) {
                    if (SessionsActivity.this.currentType == 0) {
                        headerCell.setText(LocaleController.getString("OtherSessions", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("OtherWebSessions", NUM));
                    }
                } else if (i == SessionsActivity.this.passwordSessionsSectionRow) {
                    headerCell.setText(LocaleController.getString("LoginAttempts", NUM));
                } else if (i == SessionsActivity.this.ttlHeaderRow) {
                    headerCell.setText(LocaleController.getString("TerminateOldSessionHeader", NUM));
                }
            } else if (itemViewType == 5) {
            } else {
                if (itemViewType != 6) {
                    SessionCell sessionCell = (SessionCell) viewHolder.itemView;
                    if (i == SessionsActivity.this.currentSessionRow) {
                        if (SessionsActivity.this.currentSession == null) {
                            sessionCell.showStub(SessionsActivity.this.globalFlickerLoadingView);
                            return;
                        }
                        TLRPC$TL_authorization access$2800 = SessionsActivity.this.currentSession;
                        if (SessionsActivity.this.sessions.isEmpty() && SessionsActivity.this.passwordSessions.isEmpty() && SessionsActivity.this.qrCodeRow == -1) {
                            z = false;
                        }
                        sessionCell.setSession(access$2800, z);
                    } else if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                        TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                        if (i == SessionsActivity.this.otherSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(tLObject, z);
                    } else if (i >= SessionsActivity.this.passwordSessionsStartRow && i < SessionsActivity.this.passwordSessionsEndRow) {
                        TLObject tLObject2 = (TLObject) SessionsActivity.this.passwordSessions.get(i - SessionsActivity.this.passwordSessionsStartRow);
                        if (i == SessionsActivity.this.passwordSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(tLObject2, z);
                    }
                } else {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (SessionsActivity.this.ttlDays > 30 && SessionsActivity.this.ttlDays <= 183) {
                        str = LocaleController.formatPluralString("Months", SessionsActivity.this.ttlDays / 30, new Object[0]);
                    } else if (SessionsActivity.this.ttlDays == 365) {
                        str = LocaleController.formatPluralString("Years", SessionsActivity.this.ttlDays / 365, new Object[0]);
                    } else {
                        str = LocaleController.formatPluralString("Weeks", SessionsActivity.this.ttlDays / 7, new Object[0]);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("IfInactiveFor", NUM), str, true, false);
                }
            }
        }

        public long getItemId(int i) {
            int hashCode;
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 0});
            } else if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 1});
            } else if (i == SessionsActivity.this.otherSessionsTerminateDetail) {
                hashCode = Arrays.hashCode(new Object[]{0, 2});
            } else if (i == SessionsActivity.this.passwordSessionsDetailRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 3});
            } else if (i == SessionsActivity.this.qrCodeDividerRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 4});
            } else if (i == SessionsActivity.this.ttlDivideRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 5});
            } else if (i == SessionsActivity.this.noOtherSessionsRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 6});
            } else if (i == SessionsActivity.this.currentSessionSectionRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 7});
            } else if (i == SessionsActivity.this.otherSessionsSectionRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 8});
            } else if (i == SessionsActivity.this.passwordSessionsSectionRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 9});
            } else if (i == SessionsActivity.this.ttlHeaderRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 10});
            } else if (i == SessionsActivity.this.currentSessionRow) {
                hashCode = Arrays.hashCode(new Object[]{0, 11});
            } else {
                if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                    TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                    if (tLObject instanceof TLRPC$TL_authorization) {
                        hashCode = Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC$TL_authorization) tLObject).hash)});
                    } else if (tLObject instanceof TLRPC$TL_webAuthorization) {
                        hashCode = Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC$TL_webAuthorization) tLObject).hash)});
                    }
                } else if (i >= SessionsActivity.this.passwordSessionsStartRow && i < SessionsActivity.this.passwordSessionsEndRow) {
                    TLObject tLObject2 = (TLObject) SessionsActivity.this.passwordSessions.get(i - SessionsActivity.this.passwordSessionsStartRow);
                    if (tLObject2 instanceof TLRPC$TL_authorization) {
                        hashCode = Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC$TL_authorization) tLObject2).hash)});
                    } else if (tLObject2 instanceof TLRPC$TL_webAuthorization) {
                        hashCode = Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC$TL_webAuthorization) tLObject2).hash)});
                    }
                } else if (i == SessionsActivity.this.qrCodeRow) {
                    hashCode = Arrays.hashCode(new Object[]{0, 12});
                } else if (i == SessionsActivity.this.ttlRow) {
                    hashCode = Arrays.hashCode(new Object[]{0, 13});
                }
                hashCode = Arrays.hashCode(new Object[]{0, -1});
            }
            return (long) hashCode;
        }

        public int getItemViewType(int i) {
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (i == SessionsActivity.this.terminateAllSessionsDetailRow || i == SessionsActivity.this.otherSessionsTerminateDetail || i == SessionsActivity.this.passwordSessionsDetailRow || i == SessionsActivity.this.qrCodeDividerRow || i == SessionsActivity.this.ttlDivideRow || i == SessionsActivity.this.noOtherSessionsRow) {
                return 1;
            }
            if (i == SessionsActivity.this.currentSessionSectionRow || i == SessionsActivity.this.otherSessionsSectionRow || i == SessionsActivity.this.passwordSessionsSectionRow || i == SessionsActivity.this.ttlHeaderRow) {
                return 2;
            }
            if (i == SessionsActivity.this.currentSessionRow) {
                return 4;
            }
            if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (i >= SessionsActivity.this.passwordSessionsStartRow && i < SessionsActivity.this.passwordSessionsEndRow) {
                return 4;
            }
            if (i == SessionsActivity.this.qrCodeRow) {
                return 5;
            }
            if (i == SessionsActivity.this.ttlRow) {
                return 6;
            }
            return 0;
        }
    }

    private class ScanQRCodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        BackupImageView imageView;
        TextView textView;

        public ScanQRCodeView(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(120, 120.0f, 1, 0.0f, 16.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener(SessionsActivity.this) {
                public void onClick(View view) {
                    if (ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation() != null && !ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().isRunning()) {
                        ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().restart();
                    }
                }
            });
            Theme.getColor("windowBackgroundWhiteBlackText");
            Theme.getColor("windowBackgroundWhite");
            Theme.getColor("featuredStickers_addButton");
            Theme.getColor("windowBackgroundWhite");
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.textView = linksTextView;
            addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 36.0f, 152.0f, 36.0f, 0.0f));
            this.textView.setGravity(1);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 15.0f);
            this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.textView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            String string = LocaleController.getString("AuthAnotherClientInfo4", NUM);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf(42);
            int i = indexOf + 1;
            int indexOf2 = string.indexOf(42, i);
            if (!(indexOf == -1 || indexOf2 == -1 || indexOf == indexOf2)) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spannableStringBuilder.replace(indexOf2, indexOf2 + 1, "");
                spannableStringBuilder.replace(indexOf, i, "");
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)), indexOf, indexOf2 - 1, 33);
            }
            String spannableStringBuilder2 = spannableStringBuilder.toString();
            int indexOf3 = spannableStringBuilder2.indexOf(42);
            int i2 = indexOf3 + 1;
            int indexOf4 = spannableStringBuilder2.indexOf(42, i2);
            if (!(indexOf3 == -1 || indexOf4 == -1 || indexOf3 == indexOf4)) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spannableStringBuilder.replace(indexOf4, indexOf4 + 1, "");
                spannableStringBuilder.replace(indexOf3, i2, "");
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherWebClientUrl", NUM)), indexOf3, indexOf4 - 1, 33);
            }
            this.textView.setText(spannableStringBuilder);
            TextView textView2 = new TextView(context);
            textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView2.setGravity(17);
            textView2.setTextSize(1, 14.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
            spannableStringBuilder3.append(".  ").append(LocaleController.getString("LinkDesktopDevice", NUM));
            spannableStringBuilder3.setSpan(new ColoredImageSpan(ContextCompat.getDrawable(getContext(), NUM)), 0, 1, 0);
            textView2.setText(spannableStringBuilder3);
            textView2.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            textView2.setOnClickListener(new SessionsActivity$ScanQRCodeView$$ExternalSyntheticLambda0(this));
            addView(textView2, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
            setSticker();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (SessionsActivity.this.getParentActivity() != null) {
                if (Build.VERSION.SDK_INT < 23 || SessionsActivity.this.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    SessionsActivity.this.openCameraScanActivity();
                    return;
                }
                SessionsActivity.this.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(276.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(SessionsActivity.this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(SessionsActivity.this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(objArr[0])) {
                setSticker();
            }
        }

        private void setSticker() {
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(SessionsActivity.this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(SessionsActivity.this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
            SvgHelper.SvgDrawable svgDrawable = null;
            TLRPC$Document tLRPC$Document = (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents.size() <= 6) ? null : tLRPC$TL_messages_stickerSet.documents.get(6);
            if (tLRPC$Document != null) {
                svgDrawable = DocumentObject.getSvgThumb(tLRPC$Document.thumbs, "emptyListPlaceholder", 0.2f);
            }
            SvgHelper.SvgDrawable svgDrawable2 = svgDrawable;
            if (svgDrawable2 != null) {
                svgDrawable2.overrideWidthAndHeight(512, 512);
            }
            if (tLRPC$Document != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "130_130", "tgs", (Drawable) svgDrawable2, (Object) tLRPC$TL_messages_stickerSet);
                this.imageView.getImageReceiver().setAutoRepeat(2);
                return;
            }
            MediaDataController.getInstance(SessionsActivity.this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, tLRPC$TL_messages_stickerSet == null);
        }
    }

    /* access modifiers changed from: private */
    public void openCameraScanActivity() {
        CameraScanActivity.showAsSheet(this, false, 2, new CameraScanActivity.CameraScanActivityDelegate() {
            private TLRPC$TL_error error = null;
            private TLObject response = null;

            public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
            }

            public void didFindQr(String str) {
                TLObject tLObject = this.response;
                if (tLObject instanceof TLRPC$TL_authorization) {
                    TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) tLObject;
                    if (((TLRPC$TL_authorization) tLObject).password_pending) {
                        SessionsActivity.this.passwordSessions.add(0, tLRPC$TL_authorization);
                        int unused = SessionsActivity.this.repeatLoad = 4;
                        SessionsActivity.this.lambda$loadSessions$17(false);
                    } else {
                        SessionsActivity.this.sessions.add(0, tLRPC$TL_authorization);
                    }
                    SessionsActivity.this.updateRows();
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    SessionsActivity.this.undoView.showWithAction(0, 11, (Object) this.response);
                } else if (this.error != null) {
                    AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda1(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$didFindQr$0() {
                String str;
                String str2 = this.error.text;
                if (str2 == null || !str2.equals("AUTH_TOKEN_EXCEPTION")) {
                    str = LocaleController.getString("ErrorOccurred", NUM) + "\n" + this.error.text;
                } else {
                    str = LocaleController.getString("AccountAlreadyLoggedIn", NUM);
                }
                AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", NUM), str);
            }

            public boolean processQr(String str, Runnable runnable) {
                this.response = null;
                this.error = null;
                AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda2(this, str, runnable), 750);
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$processQr$4(String str, Runnable runnable) {
                try {
                    byte[] decode = Base64.decode(str.substring(17).replaceAll("\\/", "_").replaceAll("\\+", "-"), 8);
                    TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
                    tLRPC$TL_auth_acceptLoginToken.token = decode;
                    SessionsActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_auth_acceptLoginToken, new SessionsActivity$5$$ExternalSyntheticLambda4(this, runnable));
                } catch (Exception e) {
                    FileLog.e("Failed to pass qr code auth", (Throwable) e);
                    AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda0(this));
                    runnable.run();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$processQr$2(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda3(this, tLObject, tLRPC$TL_error, runnable));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$processQr$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, Runnable runnable) {
                this.response = tLObject;
                this.error = tLRPC$TL_error;
                runnable.run();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$processQr$3() {
                AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", NUM), LocaleController.getString("ErrorOccurred", NUM));
            }
        });
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return arrayList;
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (getParentActivity() == null || i != 34) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            new AlertDialog.Builder((Context) getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString("QRCodePermissionNoCameraWithHint", NUM))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new SessionsActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).show();
        } else {
            openCameraScanActivity();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$20(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
