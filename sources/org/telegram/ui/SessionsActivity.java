package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.SessionBottomSheet;

public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private final int VIEW_TYPE_HEADER = 2;
    private final int VIEW_TYPE_INFO = 1;
    private final int VIEW_TYPE_SCANQR = 5;
    private final int VIEW_TYPE_SESSION = 4;
    private final int VIEW_TYPE_SETTINGS = 6;
    private final int VIEW_TYPE_TEXT = 0;
    /* access modifiers changed from: private */
    public TLRPC.TL_authorization currentSession;
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

    public SessionsActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
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
            public void onItemClick(int id) {
                if (id == -1) {
                    SessionsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setDurations(150);
        itemAnimator.setMoveInterpolator(CubicBezierInterpolator.DEFAULT);
        itemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        this.listView.setItemAnimator(itemAnimator);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SessionsActivity$$ExternalSyntheticLambda12(this));
        if (this.currentType == 0) {
            AnonymousClass3 r2 = new UndoView(context) {
                public void hide(boolean apply, int animated) {
                    if (!apply) {
                        TLRPC.TL_authorization authorization = (TLRPC.TL_authorization) getCurrentInfoObject();
                        TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                        req.hash = authorization.hash;
                        ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, new SessionsActivity$3$$ExternalSyntheticLambda1(this, authorization));
                    }
                    super.hide(apply, animated);
                }

                /* renamed from: lambda$hide$1$org-telegram-ui-SessionsActivity$3  reason: not valid java name */
                public /* synthetic */ void m4605lambda$hide$1$orgtelegramuiSessionsActivity$3(TLRPC.TL_authorization authorization, TLObject response, TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new SessionsActivity$3$$ExternalSyntheticLambda0(this, error, authorization));
                }

                /* renamed from: lambda$hide$0$org-telegram-ui-SessionsActivity$3  reason: not valid java name */
                public /* synthetic */ void m4604lambda$hide$0$orgtelegramuiSessionsActivity$3(TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
                    if (error == null) {
                        SessionsActivity.this.sessions.remove(authorization);
                        SessionsActivity.this.passwordSessions.remove(authorization);
                        SessionsActivity.this.updateRows();
                        if (SessionsActivity.this.listAdapter != null) {
                            SessionsActivity.this.listAdapter.notifyDataSetChanged();
                        }
                        SessionsActivity.this.m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(true);
                    }
                }
            };
            this.undoView = r2;
            frameLayout.addView(r2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4589lambda$createView$13$orgtelegramuiSessionsActivity(View view, int position) {
        String buttonText;
        String name;
        TLRPC.TL_authorization authorization;
        String buttonText2;
        int selected;
        int i = position;
        if (i == this.ttlRow) {
            if (getParentActivity() != null) {
                int i2 = this.ttlDays;
                if (i2 <= 7) {
                    selected = 0;
                } else if (i2 <= 93) {
                    selected = 1;
                } else if (i2 <= 183) {
                    selected = 2;
                } else {
                    selected = 3;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("SessionsSelfDestruct", NUM));
                String[] items = {LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
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
                    cell.setOnClickListener(new SessionsActivity$$ExternalSyntheticLambda14(this, builder));
                    a++;
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (i == this.terminateAllSessionsRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                if (this.currentType == 0) {
                    builder2.setMessage(LocaleController.getString("AreYouSureSessions", NUM));
                    builder2.setTitle(LocaleController.getString("AreYouSureSessionsTitle", NUM));
                    buttonText2 = LocaleController.getString("Terminate", NUM);
                } else {
                    builder2.setMessage(LocaleController.getString("AreYouSureWebSessions", NUM));
                    builder2.setTitle(LocaleController.getString("TerminateWebSessionsTitle", NUM));
                    buttonText2 = LocaleController.getString("Disconnect", NUM);
                }
                builder2.setPositiveButton(buttonText2, new SessionsActivity$$ExternalSyntheticLambda0(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog alertDialog = builder2.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (((i >= this.otherSessionsStartRow && i < this.otherSessionsEndRow) || ((i >= this.passwordSessionsStartRow && i < this.passwordSessionsEndRow) || i == this.currentSessionRow)) && getParentActivity() != null) {
            if (this.currentType == 0) {
                boolean isCurrentSession = false;
                if (i == this.currentSessionRow) {
                    authorization = this.currentSession;
                    isCurrentSession = true;
                } else {
                    int i3 = this.otherSessionsStartRow;
                    if (i < i3 || i >= this.otherSessionsEndRow) {
                        authorization = (TLRPC.TL_authorization) this.passwordSessions.get(i - this.passwordSessionsStartRow);
                    } else {
                        authorization = (TLRPC.TL_authorization) this.sessions.get(i - i3);
                    }
                }
                showSessionBottomSheet(authorization, isCurrentSession);
                return;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            boolean[] param = new boolean[1];
            if (this.currentType == 0) {
                builder3.setMessage(LocaleController.getString("TerminateSessionText", NUM));
                builder3.setTitle(LocaleController.getString("AreYouSureSessionTitle", NUM));
                buttonText = LocaleController.getString("Terminate", NUM);
            } else {
                TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
                builder3.setMessage(LocaleController.formatString("TerminateWebSessionText", NUM, authorization2.domain));
                builder3.setTitle(LocaleController.getString("TerminateWebSessionTitle", NUM));
                String buttonText3 = LocaleController.getString("Disconnect", NUM);
                FrameLayout frameLayout1 = new FrameLayout(getParentActivity());
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(authorization2.bot_id));
                if (user != null) {
                    name = UserObject.getFirstName(user);
                } else {
                    name = "";
                }
                CheckBoxCell cell2 = new CheckBoxCell(getParentActivity(), 1);
                cell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell2.setText(LocaleController.formatString("TerminateWebSessionStop", NUM, name), "", false, false);
                cell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout1.addView(cell2, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                cell2.setOnClickListener(new SessionsActivity$$ExternalSyntheticLambda15(param));
                builder3.setCustomViewOffset(16);
                builder3.setView(frameLayout1);
                buttonText = buttonText3;
            }
            builder3.setPositiveButton(buttonText, new SessionsActivity$$ExternalSyntheticLambda13(this, i, param));
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog2 = builder3.create();
            showDialog(alertDialog2);
            TextView button2 = (TextView) alertDialog2.getButton(-1);
            if (button2 != null) {
                button2.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4585lambda$createView$1$orgtelegramuiSessionsActivity(AlertDialog.Builder builder, View v) {
        builder.getDismissRunnable().run();
        Integer which = (Integer) v.getTag();
        int value = 0;
        if (which.intValue() == 0) {
            value = 7;
        } else if (which.intValue() == 1) {
            value = 90;
        } else if (which.intValue() == 2) {
            value = 183;
        } else if (which.intValue() == 3) {
            value = 365;
        }
        TLRPC.TL_account_setAuthorizationTTL req = new TLRPC.TL_account_setAuthorizationTTL();
        req.authorization_ttl_days = value;
        this.ttlDays = value;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        getConnectionsManager().sendRequest(req, SessionsActivity$$ExternalSyntheticLambda10.INSTANCE);
    }

    static /* synthetic */ void lambda$createView$0(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4594lambda$createView$6$orgtelegramuiSessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_resetAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda4(this));
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWebAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4591lambda$createView$3$orgtelegramuiSessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda16(this, error, response));
        for (int a = 0; a < 4; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            if (userConfig.isClientActivated()) {
                userConfig.registeredForPush = false;
                userConfig.saveConfig(false);
                MessagesController.getInstance(a).registerForPush(SharedConfig.pushString);
                ConnectionsManager.getInstance(a).setUserId(userConfig.getClientUserId());
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4590lambda$createView$2$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null && error == null && (response instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("AllSessionsTerminated", NUM)).show();
            m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4593lambda$createView$5$orgtelegramuiSessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda17(this, error, response));
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4592lambda$createView$4$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("UnknownError", NUM)).show();
            } else {
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("AllWebSessionsTerminated", NUM)).show();
            }
            m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
        }
    }

    static /* synthetic */ void lambda$createView$7(boolean[] param, View v) {
        if (v.isEnabled()) {
            param[0] = !param[0];
            ((CheckBoxCell) v).setChecked(param[0], true);
        }
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4588lambda$createView$12$orgtelegramuiSessionsActivity(int position, boolean[] param, DialogInterface dialogInterface, int option) {
        TLRPC.TL_authorization authorization;
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            if (this.currentType == 0) {
                int i = this.otherSessionsStartRow;
                if (position < i || position >= this.otherSessionsEndRow) {
                    authorization = (TLRPC.TL_authorization) this.passwordSessions.get(position - this.passwordSessionsStartRow);
                } else {
                    authorization = (TLRPC.TL_authorization) this.sessions.get(position - i);
                }
                TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                req.hash = authorization.hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SessionsActivity$$ExternalSyntheticLambda6(this, progressDialog, authorization));
                return;
            }
            TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow);
            TLRPC.TL_account_resetWebAuthorization req2 = new TLRPC.TL_account_resetWebAuthorization();
            req2.hash = authorization2.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new SessionsActivity$$ExternalSyntheticLambda7(this, progressDialog, authorization2));
            if (param[0]) {
                MessagesController.getInstance(this.currentAccount).blockPeer(authorization2.bot_id);
            }
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4596lambda$createView$9$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_authorization authorization, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda20(this, progressDialog, error, authorization));
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4595lambda$createView$8$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            this.passwordSessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4587lambda$createView$11$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_webAuthorization authorization, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda1(this, progressDialog, error, authorization));
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4586lambda$createView$10$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_webAuthorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    private void showSessionBottomSheet(TLRPC.TL_authorization authorization, boolean isCurrentSession) {
        if (authorization != null) {
            new SessionBottomSheet(this, authorization, isCurrentSession, new SessionBottomSheet.Callback() {
                public void onSessionTerminated(TLRPC.TL_authorization authorization) {
                    SessionsActivity.this.sessions.remove(authorization);
                    SessionsActivity.this.passwordSessions.remove(authorization);
                    SessionsActivity.this.updateRows();
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                    req.hash = authorization.hash;
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, SessionsActivity$4$$ExternalSyntheticLambda1.INSTANCE);
                }

                static /* synthetic */ void lambda$onSessionTerminated$0() {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: loadSessions */
    public void m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(boolean silent) {
        if (!this.loading) {
            if (!silent) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda8(this, silent)), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getWebAuthorizations(), new SessionsActivity$$ExternalSyntheticLambda9(this, silent)), this.classGuid);
        }
    }

    /* renamed from: lambda$loadSessions$16$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4599lambda$loadSessions$16$orgtelegramuiSessionsActivity(boolean silent, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda18(this, error, response, silent));
    }

    /* renamed from: lambda$loadSessions$15$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4598lambda$loadSessions$15$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response, boolean silent) {
        this.loading = false;
        int itemCount = this.listAdapter.getItemCount();
        if (error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TLRPC.TL_account_authorizations res = (TLRPC.TL_account_authorizations) response;
            int N = res.authorizations.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_authorization authorization = res.authorizations.get(a);
                if ((authorization.flags & 1) != 0) {
                    this.currentSession = authorization;
                } else if (authorization.password_pending) {
                    this.passwordSessions.add(authorization);
                } else {
                    this.sessions.add(authorization);
                }
            }
            this.ttlDays = res.authorization_ttl_days;
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
                AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda2(this, silent), 2500);
            }
        }
    }

    /* renamed from: lambda$loadSessions$19$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4602lambda$loadSessions$19$orgtelegramuiSessionsActivity(boolean silent, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda19(this, error, response, silent));
    }

    /* renamed from: lambda$loadSessions$18$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4601lambda$loadSessions$18$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response, boolean silent) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            TLRPC.TL_account_webAuthorizations res = (TLRPC.TL_account_webAuthorizations) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.sessions.addAll(res.authorizations);
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
                AndroidUtilities.runOnUIThread(new SessionsActivity$$ExternalSyntheticLambda3(this, silent), 2500);
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SessionsActivity.this.terminateAllSessionsRow || (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) || ((position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) || position == SessionsActivity.this.currentSessionRow || position == SessionsActivity.this.ttlRow);
        }

        public int getItemCount() {
            return SessionsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new ScanQRCodeView(SessionsActivity.this, this.mContext);
                    break;
                case 6:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
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
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsRow) {
                        textCell.setColors("windowBackgroundWhiteRedText2", "windowBackgroundWhiteRedText2");
                        textCell.setTag("windowBackgroundWhiteRedText2");
                        if (SessionsActivity.this.currentType == 0) {
                            textCell.setTextAndIcon(LocaleController.getString("TerminateAllSessions", NUM), NUM, false);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("TerminateAllWebSessions", NUM), NUM, false);
                            return;
                        }
                    } else if (position == SessionsActivity.this.qrCodeRow) {
                        textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                        textCell.setTag("windowBackgroundWhiteBlueText4");
                        textCell.setTextAndIcon(LocaleController.getString("AuthAnotherClient", NUM), NUM, true ^ SessionsActivity.this.sessions.isEmpty());
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    privacyCell.setFixedSize(0);
                    if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", NUM));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType != 0) {
                            privacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", NUM));
                        } else if (SessionsActivity.this.sessions.isEmpty()) {
                            privacyCell.setText("");
                        } else {
                            privacyCell.setText(LocaleController.getString("SessionsListInfo", NUM));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == SessionsActivity.this.passwordSessionsDetailRow) {
                        privacyCell.setText(LocaleController.getString("LoginAttemptsInfo", NUM));
                        if (SessionsActivity.this.otherSessionsTerminateDetail == -1) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        }
                    } else if (position == SessionsActivity.this.qrCodeDividerRow || position == SessionsActivity.this.ttlDivideRow || position == SessionsActivity.this.noOtherSessionsRow) {
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        privacyCell.setText("");
                        privacyCell.setFixedSize(12);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == SessionsActivity.this.currentSessionSectionRow) {
                        headerCell.setText(LocaleController.getString("CurrentSession", NUM));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsSectionRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("OtherSessions", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("OtherWebSessions", NUM));
                            return;
                        }
                    } else if (position == SessionsActivity.this.passwordSessionsSectionRow) {
                        headerCell.setText(LocaleController.getString("LoginAttempts", NUM));
                        return;
                    } else if (position == SessionsActivity.this.ttlHeaderRow) {
                        headerCell.setText(LocaleController.getString("TerminateOldSessionHeader", NUM));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    return;
                case 6:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (SessionsActivity.this.ttlDays > 30 && SessionsActivity.this.ttlDays <= 183) {
                        value = LocaleController.formatPluralString("Months", SessionsActivity.this.ttlDays / 30, new Object[0]);
                    } else if (SessionsActivity.this.ttlDays == 365) {
                        value = LocaleController.formatPluralString("Years", SessionsActivity.this.ttlDays / 365, new Object[0]);
                    } else {
                        value = LocaleController.formatPluralString("Weeks", SessionsActivity.this.ttlDays / 7, new Object[0]);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("IfInactiveFor", NUM), value, true, false);
                    return;
                default:
                    SessionCell sessionCell = (SessionCell) holder.itemView;
                    if (position == SessionsActivity.this.currentSessionRow) {
                        if (SessionsActivity.this.currentSession == null) {
                            sessionCell.showStub(SessionsActivity.this.globalFlickerLoadingView);
                            return;
                        }
                        TLRPC.TL_authorization access$2800 = SessionsActivity.this.currentSession;
                        if (SessionsActivity.this.sessions.isEmpty() && SessionsActivity.this.passwordSessions.isEmpty() && SessionsActivity.this.qrCodeRow == -1) {
                            z = false;
                        }
                        sessionCell.setSession(access$2800, z);
                        return;
                    } else if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                        TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                        if (position == SessionsActivity.this.otherSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(tLObject, z);
                        return;
                    } else if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                        TLObject tLObject2 = (TLObject) SessionsActivity.this.passwordSessions.get(position - SessionsActivity.this.passwordSessionsStartRow);
                        if (position == SessionsActivity.this.passwordSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(tLObject2, z);
                        return;
                    } else {
                        return;
                    }
            }
        }

        public long getItemId(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return (long) Arrays.hashCode(new Object[]{0, 0});
            } else if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                return (long) Arrays.hashCode(new Object[]{0, 1});
            } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                return (long) Arrays.hashCode(new Object[]{0, 2});
            } else if (position == SessionsActivity.this.passwordSessionsDetailRow) {
                return (long) Arrays.hashCode(new Object[]{0, 3});
            } else if (position == SessionsActivity.this.qrCodeDividerRow) {
                return (long) Arrays.hashCode(new Object[]{0, 4});
            } else if (position == SessionsActivity.this.ttlDivideRow) {
                return (long) Arrays.hashCode(new Object[]{0, 5});
            } else if (position == SessionsActivity.this.noOtherSessionsRow) {
                return (long) Arrays.hashCode(new Object[]{0, 6});
            } else if (position == SessionsActivity.this.currentSessionSectionRow) {
                return (long) Arrays.hashCode(new Object[]{0, 7});
            } else if (position == SessionsActivity.this.otherSessionsSectionRow) {
                return (long) Arrays.hashCode(new Object[]{0, 8});
            } else if (position == SessionsActivity.this.passwordSessionsSectionRow) {
                return (long) Arrays.hashCode(new Object[]{0, 9});
            } else if (position == SessionsActivity.this.ttlHeaderRow) {
                return (long) Arrays.hashCode(new Object[]{0, 10});
            } else if (position == SessionsActivity.this.currentSessionRow) {
                return (long) Arrays.hashCode(new Object[]{0, 11});
            } else {
                if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                    TLObject session = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                    if (session instanceof TLRPC.TL_authorization) {
                        return (long) Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC.TL_authorization) session).hash)});
                    } else if (session instanceof TLRPC.TL_webAuthorization) {
                        return (long) Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC.TL_webAuthorization) session).hash)});
                    }
                } else if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                    TLObject session2 = (TLObject) SessionsActivity.this.passwordSessions.get(position - SessionsActivity.this.passwordSessionsStartRow);
                    if (session2 instanceof TLRPC.TL_authorization) {
                        return (long) Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC.TL_authorization) session2).hash)});
                    } else if (session2 instanceof TLRPC.TL_webAuthorization) {
                        return (long) Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC.TL_webAuthorization) session2).hash)});
                    }
                } else if (position == SessionsActivity.this.qrCodeRow) {
                    return (long) Arrays.hashCode(new Object[]{0, 12});
                } else if (position == SessionsActivity.this.ttlRow) {
                    return (long) Arrays.hashCode(new Object[]{0, 13});
                }
                return (long) Arrays.hashCode(new Object[]{0, -1});
            }
        }

        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position == SessionsActivity.this.terminateAllSessionsDetailRow || position == SessionsActivity.this.otherSessionsTerminateDetail || position == SessionsActivity.this.passwordSessionsDetailRow || position == SessionsActivity.this.qrCodeDividerRow || position == SessionsActivity.this.ttlDivideRow || position == SessionsActivity.this.noOtherSessionsRow) {
                return 1;
            }
            if (position == SessionsActivity.this.currentSessionSectionRow || position == SessionsActivity.this.otherSessionsSectionRow || position == SessionsActivity.this.passwordSessionsSectionRow || position == SessionsActivity.this.ttlHeaderRow) {
                return 2;
            }
            if (position == SessionsActivity.this.currentSessionRow) {
                return 4;
            }
            if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                return 4;
            }
            if (position == SessionsActivity.this.qrCodeRow) {
                return 5;
            }
            if (position == SessionsActivity.this.ttlRow) {
                return 6;
            }
            return 0;
        }
    }

    private class ScanQRCodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        BackupImageView imageView;
        TextView textView;
        final /* synthetic */ SessionsActivity this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public ScanQRCodeView(org.telegram.ui.SessionsActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
                r3.<init>(r2)
                r0.imageView = r3
                r4 = 120(0x78, float:1.68E-43)
                r5 = 1123024896(0x42var_, float:120.0)
                r6 = 1
                r7 = 0
                r8 = 1098907648(0x41800000, float:16.0)
                r9 = 0
                r10 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
                r0.addView(r3, r4)
                org.telegram.ui.Components.BackupImageView r3 = r0.imageView
                org.telegram.ui.SessionsActivity$ScanQRCodeView$1 r4 = new org.telegram.ui.SessionsActivity$ScanQRCodeView$1
                r4.<init>(r1)
                r3.setOnClickListener(r4)
                r1 = 8
                int[] r1 = new int[r1]
                r3 = 3355443(0x333333, float:4.701977E-39)
                r4 = 0
                r1[r4] = r3
                java.lang.String r3 = "windowBackgroundWhiteBlackText"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1[r6] = r5
                r5 = 2
                r7 = 16777215(0xffffff, float:2.3509886E-38)
                r1[r5] = r7
                java.lang.String r5 = "windowBackgroundWhite"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r8 = 3
                r1[r8] = r7
                r7 = 4
                r8 = 5285866(0x50a7ea, float:7.407076E-39)
                r1[r7] = r8
                java.lang.String r7 = "featuredStickers_addButton"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r9 = 5
                r1[r9] = r8
                r8 = 6
                r9 = 2170912(0x212020, float:3.042096E-39)
                r1[r8] = r9
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r9 = 7
                r1[r9] = r8
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r8 = new org.telegram.ui.Components.LinkSpanDrawable$LinksTextView
                r8.<init>(r2)
                r0.textView = r8
                r9 = -1
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 0
                r12 = 1108344832(0x42100000, float:36.0)
                r13 = 1125646336(0x43180000, float:152.0)
                r14 = 1108344832(0x42100000, float:36.0)
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r8, r9)
                android.widget.TextView r8 = r0.textView
                r8.setGravity(r6)
                android.widget.TextView r8 = r0.textView
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r8.setTextColor(r3)
                android.widget.TextView r3 = r0.textView
                r8 = 1097859072(0x41700000, float:15.0)
                r3.setTextSize(r6, r8)
                android.widget.TextView r3 = r0.textView
                java.lang.String r8 = "windowBackgroundWhiteLinkText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setLinkTextColor(r8)
                android.widget.TextView r3 = r0.textView
                java.lang.String r8 = "windowBackgroundWhiteLinkSelection"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r3.setHighlightColor(r8)
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r0.setBackgroundColor(r3)
                java.lang.String r3 = "AuthAnotherClientInfo4"
                r5 = 2131624529(0x7f0e0251, float:1.887624E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)
                android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
                r5.<init>(r3)
                r8 = 42
                int r9 = r3.indexOf(r8)
                int r10 = r9 + 1
                int r10 = r3.indexOf(r8, r10)
                r11 = 33
                java.lang.String r12 = ""
                r13 = -1
                if (r9 == r13) goto L_0x0101
                if (r10 == r13) goto L_0x0101
                if (r9 == r10) goto L_0x0101
                android.widget.TextView r14 = r0.textView
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r15 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r15.<init>()
                r14.setMovementMethod(r15)
                int r14 = r10 + 1
                r5.replace(r10, r14, r12)
                int r14 = r9 + 1
                r5.replace(r9, r14, r12)
                org.telegram.ui.Components.URLSpanNoUnderline r14 = new org.telegram.ui.Components.URLSpanNoUnderline
                r15 = 2131624525(0x7f0e024d, float:1.8876232E38)
                java.lang.String r6 = "AuthAnotherClientDownloadClientUrl"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r15)
                r14.<init>(r6)
                int r6 = r10 + -1
                r5.setSpan(r14, r9, r6, r11)
            L_0x0101:
                java.lang.String r3 = r5.toString()
                int r6 = r3.indexOf(r8)
                int r9 = r6 + 1
                int r8 = r3.indexOf(r8, r9)
                if (r6 == r13) goto L_0x013c
                if (r8 == r13) goto L_0x013c
                if (r6 == r8) goto L_0x013c
                android.widget.TextView r9 = r0.textView
                org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r10 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                r10.<init>()
                r9.setMovementMethod(r10)
                int r9 = r8 + 1
                r5.replace(r8, r9, r12)
                int r9 = r6 + 1
                r5.replace(r6, r9, r12)
                org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline
                r10 = 2131624536(0x7f0e0258, float:1.8876254E38)
                java.lang.String r12 = "AuthAnotherWebClientUrl"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
                r9.<init>(r10)
                int r10 = r8 + -1
                r5.setSpan(r9, r6, r10, r11)
            L_0x013c:
                android.widget.TextView r9 = r0.textView
                r9.setText(r5)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                r10 = 1107820544(0x42080000, float:34.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.setPadding(r11, r4, r10, r4)
                r10 = 17
                r9.setGravity(r10)
                r10 = 1096810496(0x41600000, float:14.0)
                r11 = 1
                r9.setTextSize(r11, r10)
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r9.setTypeface(r10)
                android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
                r10.<init>()
                java.lang.String r11 = ".  "
                android.text.SpannableStringBuilder r11 = r10.append(r11)
                r12 = 2131626437(0x7f0e09c5, float:1.888011E38)
                java.lang.String r13 = "LinkDesktopDevice"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                r11.append(r12)
                org.telegram.ui.Components.ColoredImageSpan r11 = new org.telegram.ui.Components.ColoredImageSpan
                android.content.Context r12 = r18.getContext()
                r13 = 2131165806(0x7var_e, float:1.794584E38)
                android.graphics.drawable.Drawable r12 = androidx.core.content.ContextCompat.getDrawable(r12, r13)
                r11.<init>((android.graphics.drawable.Drawable) r12)
                r12 = 1
                r10.setSpan(r11, r4, r12, r4)
                r9.setText(r10)
                java.lang.String r4 = "featuredStickers_buttonText"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r9.setTextColor(r4)
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                java.lang.String r11 = "featuredStickers_addButtonPressed"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r4, r7, r11)
                r9.setBackgroundDrawable(r4)
                org.telegram.ui.SessionsActivity$ScanQRCodeView$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.SessionsActivity$ScanQRCodeView$$ExternalSyntheticLambda0
                r4.<init>(r0)
                r9.setOnClickListener(r4)
                r11 = -1
                r12 = 1111490560(0x42400000, float:48.0)
                r13 = 80
                r14 = 1098907648(0x41800000, float:16.0)
                r15 = 1097859072(0x41700000, float:15.0)
                r16 = 1098907648(0x41800000, float:16.0)
                r17 = 1098907648(0x41800000, float:16.0)
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r9, r4)
                r18.setSticker()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SessionsActivity.ScanQRCodeView.<init>(org.telegram.ui.SessionsActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-SessionsActivity$ScanQRCodeView  reason: not valid java name */
        public /* synthetic */ void m4611lambda$new$0$orgtelegramuiSessionsActivity$ScanQRCodeView(View view) {
            if (this.this$0.getParentActivity() != null) {
                if (Build.VERSION.SDK_INT < 23 || this.this$0.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    this.this$0.openCameraScanActivity();
                    return;
                }
                this.this$0.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(276.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(this.this$0.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.this$0.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(args[0])) {
                setSticker();
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.tgnet.TLRPC$Document} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void setSticker() {
            /*
                r11 = this;
                r0 = 0
                r1 = 0
                r2 = 0
                org.telegram.ui.SessionsActivity r3 = r11.this$0
                int r3 = r3.currentAccount
                org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
                java.lang.String r4 = "tg_placeholders_android"
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByName(r4)
                if (r2 != 0) goto L_0x0023
                org.telegram.ui.SessionsActivity r3 = r11.this$0
                int r3 = r3.currentAccount
                org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByEmojiOrName(r4)
            L_0x0023:
                if (r2 == 0) goto L_0x0037
                java.util.ArrayList r3 = r2.documents
                int r3 = r3.size()
                r5 = 6
                if (r3 <= r5) goto L_0x0037
                java.util.ArrayList r3 = r2.documents
                java.lang.Object r3 = r3.get(r5)
                r1 = r3
                org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC.Document) r1
            L_0x0037:
                java.lang.String r0 = "130_130"
                r3 = 0
                if (r1 == 0) goto L_0x0047
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r1.thumbs
                r6 = 1045220557(0x3e4ccccd, float:0.2)
                java.lang.String r7 = "emptyListPlaceholder"
                org.telegram.messenger.SvgHelper$SvgDrawable r3 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize>) r5, (java.lang.String) r7, (float) r6)
            L_0x0047:
                if (r3 == 0) goto L_0x004e
                r5 = 512(0x200, float:7.175E-43)
                r3.overrideWidthAndHeight(r5, r5)
            L_0x004e:
                if (r1 == 0) goto L_0x006a
                org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForDocument(r1)
                org.telegram.ui.Components.BackupImageView r5 = r11.imageView
                java.lang.String r8 = "tgs"
                r6 = r4
                r7 = r0
                r9 = r3
                r10 = r2
                r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
                org.telegram.ui.Components.BackupImageView r5 = r11.imageView
                org.telegram.messenger.ImageReceiver r5 = r5.getImageReceiver()
                r6 = 2
                r5.setAutoRepeat(r6)
                goto L_0x007d
            L_0x006a:
                org.telegram.ui.SessionsActivity r5 = r11.this$0
                int r5 = r5.currentAccount
                org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.getInstance(r5)
                r6 = 0
                if (r2 != 0) goto L_0x0079
                r7 = 1
                goto L_0x007a
            L_0x0079:
                r7 = 0
            L_0x007a:
                r5.loadStickersByEmojiOrName(r4, r6, r7)
            L_0x007d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SessionsActivity.ScanQRCodeView.setSticker():void");
        }
    }

    /* access modifiers changed from: private */
    public void openCameraScanActivity() {
        CameraScanActivity.showAsSheet(this, false, 2, new CameraScanActivity.CameraScanActivityDelegate() {
            private TLRPC.TL_error error = null;
            private TLObject response = null;

            public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
            }

            public void didFindQr(String link) {
                TLObject tLObject = this.response;
                if (tLObject instanceof TLRPC.TL_authorization) {
                    TLRPC.TL_authorization authorization = (TLRPC.TL_authorization) tLObject;
                    if (((TLRPC.TL_authorization) tLObject).password_pending) {
                        SessionsActivity.this.passwordSessions.add(0, authorization);
                        int unused = SessionsActivity.this.repeatLoad = 4;
                        SessionsActivity.this.m4600lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
                    } else {
                        SessionsActivity.this.sessions.add(0, authorization);
                    }
                    SessionsActivity.this.updateRows();
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    SessionsActivity.this.undoView.showWithAction(0, 11, (Object) this.response);
                } else if (this.error != null) {
                    AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda0(this));
                }
            }

            /* renamed from: lambda$didFindQr$0$org-telegram-ui-SessionsActivity$5  reason: not valid java name */
            public /* synthetic */ void m4606lambda$didFindQr$0$orgtelegramuiSessionsActivity$5() {
                String text;
                if (this.error.text == null || !this.error.text.equals("AUTH_TOKEN_EXCEPTION")) {
                    text = LocaleController.getString("ErrorOccurred", NUM) + "\n" + this.error.text;
                } else {
                    text = LocaleController.getString("AccountAlreadyLoggedIn", NUM);
                }
                AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", NUM), text);
            }

            public boolean processQr(String link, Runnable onLoadEnd) {
                this.response = null;
                this.error = null;
                AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda2(this, link, onLoadEnd), 750);
                return true;
            }

            /* renamed from: lambda$processQr$4$org-telegram-ui-SessionsActivity$5  reason: not valid java name */
            public /* synthetic */ void m4610lambda$processQr$4$orgtelegramuiSessionsActivity$5(String link, Runnable onLoadEnd) {
                try {
                    byte[] token = Base64.decode(link.substring("tg://login?token=".length()).replaceAll("\\/", "_").replaceAll("\\+", "-"), 8);
                    TLRPC.TL_auth_acceptLoginToken req = new TLRPC.TL_auth_acceptLoginToken();
                    req.token = token;
                    SessionsActivity.this.getConnectionsManager().sendRequest(req, new SessionsActivity$5$$ExternalSyntheticLambda4(this, onLoadEnd));
                } catch (Exception e) {
                    FileLog.e("Failed to pass qr code auth", (Throwable) e);
                    AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda1(this));
                    onLoadEnd.run();
                }
            }

            /* renamed from: lambda$processQr$2$org-telegram-ui-SessionsActivity$5  reason: not valid java name */
            public /* synthetic */ void m4608lambda$processQr$2$orgtelegramuiSessionsActivity$5(Runnable onLoadEnd, TLObject response2, TLRPC.TL_error error2) {
                AndroidUtilities.runOnUIThread(new SessionsActivity$5$$ExternalSyntheticLambda3(this, response2, error2, onLoadEnd));
            }

            /* renamed from: lambda$processQr$1$org-telegram-ui-SessionsActivity$5  reason: not valid java name */
            public /* synthetic */ void m4607lambda$processQr$1$orgtelegramuiSessionsActivity$5(TLObject response2, TLRPC.TL_error error2, Runnable onLoadEnd) {
                this.response = response2;
                this.error = error2;
                onLoadEnd.run();
            }

            /* renamed from: lambda$processQr$3$org-telegram-ui-SessionsActivity$5  reason: not valid java name */
            public /* synthetic */ void m4609lambda$processQr$3$orgtelegramuiSessionsActivity$5() {
                AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", NUM), LocaleController.getString("ErrorOccurred", NUM));
            }
        });
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return themeDescriptions;
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (getParentActivity() == null || requestCode != 34) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            new AlertDialog.Builder((Context) getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString("QRCodePermissionNoCameraWithHint", NUM))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new SessionsActivity$$ExternalSyntheticLambda11(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).show();
        } else {
            openCameraScanActivity();
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$20$org-telegram-ui-SessionsActivity  reason: not valid java name */
    public /* synthetic */ void m4603xcdb187e2(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
