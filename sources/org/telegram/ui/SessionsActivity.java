package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_authorizations;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_getWebAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC$TL_account_resetWebAuthorization;
import org.telegram.tgnet.TLRPC$TL_account_resetWebAuthorizations;
import org.telegram.tgnet.TLRPC$TL_account_webAuthorizations;
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionIntroActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.SessionsActivity;

public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public TLRPC$TL_authorization currentSession;
    /* access modifiers changed from: private */
    public int currentSessionRow;
    /* access modifiers changed from: private */
    public int currentSessionSectionRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public LinearLayout emptyLayout;
    private EmptyTextProgressView emptyView;
    private ImageView imageView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
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
    public int qrCodeRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> sessions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int terminateAllSessionsDetailRow;
    /* access modifiers changed from: private */
    public int terminateAllSessionsRow;
    private TextView textView1;
    private TextView textView2;
    private UndoView undoView;

    public SessionsActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        loadSessions(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public View createView(Context context) {
        Context context2 = context;
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
        this.listAdapter = new ListAdapter(context2);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        if (this.currentType == 0) {
            imageView2.setImageResource(NUM);
        } else {
            imageView2.setImageResource(NUM);
        }
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sessions_devicesImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.textView1 = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.textView1.setGravity(17);
        this.textView1.setTextSize(1, 17.0f);
        this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (this.currentType == 0) {
            this.textView1.setText(LocaleController.getString("NoOtherSessions", NUM));
        } else {
            this.textView1.setText(LocaleController.getString("NoOtherWebSessions", NUM));
        }
        this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        TextView textView3 = new TextView(context2);
        this.textView2 = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.textView2.setGravity(17);
        this.textView2.setTextSize(1, 17.0f);
        this.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (this.currentType == 0) {
            this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", NUM));
        } else {
            this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", NUM));
        }
        this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SessionsActivity.this.lambda$createView$15$SessionsActivity(view, i);
            }
        });
        if (this.currentType == 0) {
            AnonymousClass2 r3 = new UndoView(context2) {
                public void hide(boolean z, int i) {
                    if (!z) {
                        TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) getCurrentInfoObject();
                        TLRPC$TL_account_resetAuthorization tLRPC$TL_account_resetAuthorization = new TLRPC$TL_account_resetAuthorization();
                        tLRPC$TL_account_resetAuthorization.hash = tLRPC$TL_authorization.hash;
                        ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(tLRPC$TL_account_resetAuthorization, new RequestDelegate(tLRPC$TL_authorization) {
                            public final /* synthetic */ TLRPC$TL_authorization f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                SessionsActivity.AnonymousClass2.this.lambda$hide$1$SessionsActivity$2(this.f$1, tLObject, tLRPC$TL_error);
                            }
                        });
                    }
                    super.hide(z, i);
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$hide$1 */
                public /* synthetic */ void lambda$hide$1$SessionsActivity$2(TLRPC$TL_authorization tLRPC$TL_authorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_authorization) {
                        public final /* synthetic */ TLRPC$TL_error f$1;
                        public final /* synthetic */ TLRPC$TL_authorization f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            SessionsActivity.AnonymousClass2.this.lambda$null$0$SessionsActivity$2(this.f$1, this.f$2);
                        }
                    });
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$null$0 */
                public /* synthetic */ void lambda$null$0$SessionsActivity$2(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
                    if (tLRPC$TL_error == null) {
                        SessionsActivity.this.sessions.remove(tLRPC$TL_authorization);
                        SessionsActivity.this.passwordSessions.remove(tLRPC$TL_authorization);
                        SessionsActivity.this.updateRows();
                        if (SessionsActivity.this.listAdapter != null) {
                            SessionsActivity.this.listAdapter.notifyDataSetChanged();
                        }
                        SessionsActivity.this.loadSessions(true);
                    }
                }
            };
            this.undoView = r3;
            frameLayout2.addView(r3, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$15 */
    public /* synthetic */ void lambda$createView$15$SessionsActivity(View view, int i) {
        String str;
        String str2;
        String str3;
        int i2 = i;
        if (i2 == this.qrCodeRow) {
            ActionIntroActivity actionIntroActivity = new ActionIntroActivity(5);
            actionIntroActivity.setQrLoginDelegate(new ActionIntroActivity.ActionIntroQRLoginDelegate() {
                public final void didFindQRCode(String str) {
                    SessionsActivity.this.lambda$null$3$SessionsActivity(str);
                }
            });
            presentFragment(actionIntroActivity);
        } else if (i2 == this.terminateAllSessionsRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                if (this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("AreYouSureSessions", NUM));
                    builder.setTitle(LocaleController.getString("AreYouSureSessionsTitle", NUM));
                    str3 = LocaleController.getString("Terminate", NUM);
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSureWebSessions", NUM));
                    builder.setTitle(LocaleController.getString("TerminateWebSessionsTitle", NUM));
                    str3 = LocaleController.getString("Disconnect", NUM);
                }
                builder.setPositiveButton(str3, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SessionsActivity.this.lambda$null$8$SessionsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (((i2 >= this.otherSessionsStartRow && i2 < this.otherSessionsEndRow) || (i2 >= this.passwordSessionsStartRow && i2 < this.passwordSessionsEndRow)) && getParentActivity() != null) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            boolean[] zArr = new boolean[1];
            if (this.currentType == 0) {
                builder2.setMessage(LocaleController.getString("TerminateSessionText", NUM));
                builder2.setTitle(LocaleController.getString("AreYouSureSessionTitle", NUM));
                str = LocaleController.getString("Terminate", NUM);
            } else {
                TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) this.sessions.get(i2 - this.otherSessionsStartRow);
                builder2.setMessage(LocaleController.formatString("TerminateWebSessionText", NUM, tLRPC$TL_webAuthorization.domain));
                builder2.setTitle(LocaleController.getString("TerminateWebSessionTitle", NUM));
                String string = LocaleController.getString("Disconnect", NUM);
                FrameLayout frameLayout = new FrameLayout(getParentActivity());
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$TL_webAuthorization.bot_id));
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
                checkBoxCell.setOnClickListener(new View.OnClickListener(zArr) {
                    public final /* synthetic */ boolean[] f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        SessionsActivity.lambda$null$9(this.f$0, view);
                    }
                });
                builder2.setCustomViewOffset(16);
                builder2.setView(frameLayout);
                str = string;
            }
            builder2.setPositiveButton(str, new DialogInterface.OnClickListener(i2, zArr) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ boolean[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    SessionsActivity.this.lambda$null$14$SessionsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create2 = builder2.create();
            showDialog(create2);
            TextView textView3 = (TextView) create2.getButton(-1);
            if (textView3 != null) {
                textView3.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$SessionsActivity(String str) {
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        getConnectionsManager().sendRequest(tLRPC$TL_auth_acceptLoginToken, new RequestDelegate(alertDialog) {
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SessionsActivity.this.lambda$null$2$SessionsActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$SessionsActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_error) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$1$SessionsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$SessionsActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (tLObject instanceof TLRPC$TL_authorization) {
            this.sessions.add(0, (TLRPC$TL_authorization) tLObject);
            updateRows();
            this.listAdapter.notifyDataSetChanged();
            this.undoView.showWithAction(0, 11, (Object) tLObject);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$0$SessionsActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$SessionsActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if (tLRPC$TL_error.text.equals("AUTH_TOKEN_EXCEPTION")) {
            str = LocaleController.getString("AccountAlreadyLoggedIn", NUM);
        } else {
            str = LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("AuthAnotherClient", NUM), str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$SessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_resetAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SessionsActivity.this.lambda$null$5$SessionsActivity(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resetWebAuthorizations(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SessionsActivity.this.lambda$null$7$SessionsActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$SessionsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$4$SessionsActivity(this.f$1, this.f$2);
            }
        });
        for (int i = 0; i < 3; i++) {
            UserConfig instance = UserConfig.getInstance(i);
            if (instance.isClientActivated()) {
                instance.registeredForPush = false;
                instance.saveConfig(false);
                MessagesController.getInstance(i).registerForPush(SharedConfig.pushString);
                ConnectionsManager.getInstance(i).setUserId(instance.getClientUserId());
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$SessionsActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (getParentActivity() != null && tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
            Toast.makeText(getParentActivity(), LocaleController.getString("TerminateAllSessions", NUM), 0).show();
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$SessionsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$6$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$6 */
    public /* synthetic */ void lambda$null$6$SessionsActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (getParentActivity() != null) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                Toast.makeText(getParentActivity(), LocaleController.getString("UnknownError", NUM), 0).show();
            } else {
                Toast.makeText(getParentActivity(), LocaleController.getString("TerminateAllWebSessions", NUM), 0).show();
            }
            finishFragment();
        }
    }

    static /* synthetic */ void lambda$null$9(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            zArr[0] = !zArr[0];
            ((CheckBoxCell) view).setChecked(zArr[0], true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ void lambda$null$14$SessionsActivity(int i, boolean[] zArr, DialogInterface dialogInterface, int i2) {
        TLRPC$TL_authorization tLRPC$TL_authorization;
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_resetAuthorization, new RequestDelegate(alertDialog, tLRPC$TL_authorization) {
                    public final /* synthetic */ AlertDialog f$1;
                    public final /* synthetic */ TLRPC$TL_authorization f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        SessionsActivity.this.lambda$null$11$SessionsActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
            TLRPC$TL_account_resetWebAuthorization tLRPC$TL_account_resetWebAuthorization = new TLRPC$TL_account_resetWebAuthorization();
            tLRPC$TL_account_resetWebAuthorization.hash = tLRPC$TL_webAuthorization.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_resetWebAuthorization, new RequestDelegate(alertDialog, tLRPC$TL_webAuthorization) {
                public final /* synthetic */ AlertDialog f$1;
                public final /* synthetic */ TLRPC$TL_webAuthorization f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SessionsActivity.this.lambda$null$13$SessionsActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
            if (zArr[0]) {
                MessagesController.getInstance(this.currentAccount).blockPeer(tLRPC$TL_webAuthorization.bot_id);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$SessionsActivity(AlertDialog alertDialog, TLRPC$TL_authorization tLRPC$TL_authorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error, tLRPC$TL_authorization) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLRPC$TL_authorization f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$10$SessionsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$SessionsActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
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
    /* renamed from: lambda$null$13 */
    public /* synthetic */ void lambda$null$13$SessionsActivity(AlertDialog alertDialog, TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLRPC$TL_error, tLRPC$TL_webAuthorization) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLRPC$TL_webAuthorization f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$12$SessionsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$SessionsActivity(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization) {
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
            loadSessions(true);
        }
    }

    /* access modifiers changed from: private */
    public void loadSessions(boolean z) {
        if (!this.loading) {
            if (!z) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getAuthorizations(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        SessionsActivity.this.lambda$loadSessions$17$SessionsActivity(tLObject, tLRPC$TL_error);
                    }
                }), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getWebAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SessionsActivity.this.lambda$loadSessions$19$SessionsActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadSessions$17 */
    public /* synthetic */ void lambda$loadSessions$17$SessionsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$16$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$SessionsActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.loading = false;
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
            updateRows();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadSessions$19 */
    public /* synthetic */ void lambda$loadSessions$19$SessionsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$18$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$18 */
    public /* synthetic */ void lambda$null$18$SessionsActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        boolean z = false;
        this.rowCount = 0;
        this.qrCodeRow = -1;
        if (this.currentSession != null) {
            int i = 0 + 1;
            this.rowCount = i;
            this.currentSessionSectionRow = 0;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (this.currentType == 0 && getMessagesController().qrLoginCamera) {
            z = true;
        }
        if (!this.passwordSessions.isEmpty() || !this.sessions.isEmpty()) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.terminateAllSessionsRow = i2;
            this.rowCount = i3 + 1;
            this.terminateAllSessionsDetailRow = i3;
            this.noOtherSessionsRow = -1;
        } else {
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            if (z) {
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.qrCodeRow = i4;
            }
            if (this.currentType == 1 || this.currentSession != null) {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.noOtherSessionsRow = i5;
            } else {
                this.noOtherSessionsRow = -1;
            }
        }
        if (this.passwordSessions.isEmpty()) {
            this.passwordSessionsDetailRow = -1;
            this.passwordSessionsEndRow = -1;
            this.passwordSessionsStartRow = -1;
            this.passwordSessionsSectionRow = -1;
        } else {
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.passwordSessionsSectionRow = i6;
            this.passwordSessionsStartRow = i7;
            int size = i7 + this.passwordSessions.size();
            this.rowCount = size;
            this.passwordSessionsEndRow = size;
            this.rowCount = size + 1;
            this.passwordSessionsDetailRow = size;
        }
        if (this.sessions.isEmpty()) {
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
        int i8 = this.rowCount;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.otherSessionsSectionRow = i8;
        if (z) {
            this.rowCount = i9 + 1;
            this.qrCodeRow = i9;
        }
        int i10 = this.rowCount;
        this.otherSessionsStartRow = i10;
        this.otherSessionsEndRow = i10 + this.sessions.size();
        int size2 = this.rowCount + this.sessions.size();
        this.rowCount = size2;
        this.rowCount = size2 + 1;
        this.otherSessionsTerminateDetail = size2;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == SessionsActivity.this.terminateAllSessionsRow || adapterPosition == SessionsActivity.this.qrCodeRow || (adapterPosition >= SessionsActivity.this.otherSessionsStartRow && adapterPosition < SessionsActivity.this.otherSessionsEndRow) || (adapterPosition >= SessionsActivity.this.passwordSessionsStartRow && adapterPosition < SessionsActivity.this.passwordSessionsEndRow);
        }

        public int getItemCount() {
            if (SessionsActivity.this.loading) {
                return 0;
            }
            return SessionsActivity.this.rowCount;
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
            } else if (i != 3) {
                view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = SessionsActivity.this.emptyLayout;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: boolean} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r6, int r7) {
            /*
                r5 = this;
                int r0 = r6.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x020b
                r3 = -1
                if (r0 == r2) goto L_0x0154
                r4 = 2
                if (r0 == r4) goto L_0x00f8
                r4 = 3
                if (r0 == r4) goto L_0x00af
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.SessionCell r6 = (org.telegram.ui.Cells.SessionCell) r6
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.currentSessionRow
                if (r7 != r0) goto L_0x0049
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                org.telegram.tgnet.TLRPC$TL_authorization r7 = r7.currentSession
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r0 = r0.sessions
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0043
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r0 = r0.passwordSessions
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0043
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.qrCodeRow
                if (r0 == r3) goto L_0x0044
            L_0x0043:
                r1 = 1
            L_0x0044:
                r6.setSession(r7, r1)
                goto L_0x0272
            L_0x0049:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.otherSessionsStartRow
                if (r7 < r0) goto L_0x007c
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.otherSessionsEndRow
                if (r7 >= r0) goto L_0x007c
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r0 = r0.sessions
                org.telegram.ui.SessionsActivity r3 = org.telegram.ui.SessionsActivity.this
                int r3 = r3.otherSessionsStartRow
                int r3 = r7 - r3
                java.lang.Object r0 = r0.get(r3)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                org.telegram.ui.SessionsActivity r3 = org.telegram.ui.SessionsActivity.this
                int r3 = r3.otherSessionsEndRow
                int r3 = r3 - r2
                if (r7 == r3) goto L_0x0077
                r1 = 1
            L_0x0077:
                r6.setSession(r0, r1)
                goto L_0x0272
            L_0x007c:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.passwordSessionsStartRow
                if (r7 < r0) goto L_0x0272
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.passwordSessionsEndRow
                if (r7 >= r0) goto L_0x0272
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r0 = r0.passwordSessions
                org.telegram.ui.SessionsActivity r3 = org.telegram.ui.SessionsActivity.this
                int r3 = r3.passwordSessionsStartRow
                int r3 = r7 - r3
                java.lang.Object r0 = r0.get(r3)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                org.telegram.ui.SessionsActivity r3 = org.telegram.ui.SessionsActivity.this
                int r3 = r3.passwordSessionsEndRow
                int r3 = r3 - r2
                if (r7 == r3) goto L_0x00aa
                r1 = 1
            L_0x00aa:
                r6.setSession(r0, r1)
                goto L_0x0272
            L_0x00af:
                org.telegram.ui.SessionsActivity r6 = org.telegram.ui.SessionsActivity.this
                android.widget.LinearLayout r6 = r6.emptyLayout
                android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
                if (r6 == 0) goto L_0x0272
                r7 = 1130102784(0x435CLASSNAME, float:220.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
                int r0 = r0.y
                int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                int r0 = r0 - r2
                org.telegram.ui.SessionsActivity r2 = org.telegram.ui.SessionsActivity.this
                int r2 = r2.qrCodeRow
                if (r2 != r3) goto L_0x00d4
                r2 = 0
                goto L_0x00d6
            L_0x00d4:
                r2 = 30
            L_0x00d6:
                int r2 = r2 + 128
                float r2 = (float) r2
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r2
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 21
                if (r2 < r3) goto L_0x00e6
                int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            L_0x00e6:
                int r0 = r0 - r1
                int r7 = java.lang.Math.max(r7, r0)
                r6.height = r7
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                android.widget.LinearLayout r7 = r7.emptyLayout
                r7.setLayoutParams(r6)
                goto L_0x0272
            L_0x00f8:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.HeaderCell r6 = (org.telegram.ui.Cells.HeaderCell) r6
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.currentSessionSectionRow
                if (r7 != r0) goto L_0x0112
                r7 = 2131625023(0x7f0e043f, float:1.8877242E38)
                java.lang.String r0 = "CurrentSession"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0272
            L_0x0112:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.otherSessionsSectionRow
                if (r7 != r0) goto L_0x013e
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                int r7 = r7.currentType
                if (r7 != 0) goto L_0x0130
                r7 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
                java.lang.String r0 = "OtherSessions"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0272
            L_0x0130:
                r7 = 2131626526(0x7f0e0a1e, float:1.888029E38)
                java.lang.String r0 = "OtherWebSessions"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0272
            L_0x013e:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.passwordSessionsSectionRow
                if (r7 != r0) goto L_0x0272
                r7 = 2131625993(0x7f0e0809, float:1.887921E38)
                java.lang.String r0 = "LoginAttempts"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0272
            L_0x0154:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r6 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r6
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.terminateAllSessionsDetailRow
                r1 = 2131165448(0x7var_, float:1.7945113E38)
                java.lang.String r2 = "windowBackgroundGrayShadow"
                if (r7 != r0) goto L_0x0192
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                int r7 = r7.currentType
                if (r7 != 0) goto L_0x017b
                r7 = 2131624897(0x7f0e03c1, float:1.8876987E38)
                java.lang.String r0 = "ClearOtherSessionsHelp"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0187
            L_0x017b:
                r7 = 2131624898(0x7f0e03c2, float:1.8876989E38)
                java.lang.String r0 = "ClearOtherWebSessionsHelp"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
            L_0x0187:
                android.content.Context r7 = r5.mContext
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r2)
                r6.setBackgroundDrawable(r7)
                goto L_0x0272
            L_0x0192:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.otherSessionsTerminateDetail
                r4 = 2131165449(0x7var_, float:1.7945115E38)
                if (r7 != r0) goto L_0x01db
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                int r7 = r7.currentType
                if (r7 != 0) goto L_0x01c4
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r7 = r7.sessions
                boolean r7 = r7.isEmpty()
                if (r7 == 0) goto L_0x01b7
                java.lang.String r7 = ""
                r6.setText(r7)
                goto L_0x01d0
            L_0x01b7:
                r7 = 2131627621(0x7f0e0e65, float:1.8882512E38)
                java.lang.String r0 = "TerminateSessionInfo"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x01d0
            L_0x01c4:
                r7 = 2131627624(0x7f0e0e68, float:1.8882518E38)
                java.lang.String r0 = "TerminateWebSessionInfo"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
            L_0x01d0:
                android.content.Context r7 = r5.mContext
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r4, (java.lang.String) r2)
                r6.setBackgroundDrawable(r7)
                goto L_0x0272
            L_0x01db:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.passwordSessionsDetailRow
                if (r7 != r0) goto L_0x0272
                r7 = 2131625994(0x7f0e080a, float:1.8879212E38)
                java.lang.String r0 = "LoginAttemptsInfo"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                int r7 = r7.otherSessionsTerminateDetail
                if (r7 != r3) goto L_0x0201
                android.content.Context r7 = r5.mContext
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r4, (java.lang.String) r2)
                r6.setBackgroundDrawable(r7)
                goto L_0x0272
            L_0x0201:
                android.content.Context r7 = r5.mContext
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r2)
                r6.setBackgroundDrawable(r7)
                goto L_0x0272
            L_0x020b:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.TextSettingsCell r6 = (org.telegram.ui.Cells.TextSettingsCell) r6
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.terminateAllSessionsRow
                if (r7 != r0) goto L_0x0246
                java.lang.String r7 = "windowBackgroundWhiteRedText2"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r6.setTextColor(r0)
                r6.setTag(r7)
                org.telegram.ui.SessionsActivity r7 = org.telegram.ui.SessionsActivity.this
                int r7 = r7.currentType
                if (r7 != 0) goto L_0x0239
                r7 = 2131627619(0x7f0e0e63, float:1.8882508E38)
                java.lang.String r0 = "TerminateAllSessions"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7, r1)
                goto L_0x0272
            L_0x0239:
                r7 = 2131627620(0x7f0e0e64, float:1.888251E38)
                java.lang.String r0 = "TerminateAllWebSessions"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7, r1)
                goto L_0x0272
            L_0x0246:
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                int r0 = r0.qrCodeRow
                if (r7 != r0) goto L_0x0272
                java.lang.String r7 = "windowBackgroundWhiteBlueText4"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r6.setTextColor(r0)
                r6.setTag(r7)
                r7 = 2131624413(0x7f0e01dd, float:1.8876005E38)
                java.lang.String r0 = "AuthAnotherClient"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                org.telegram.ui.SessionsActivity r0 = org.telegram.ui.SessionsActivity.this
                java.util.ArrayList r0 = r0.sessions
                boolean r0 = r0.isEmpty()
                r0 = r0 ^ r2
                r6.setText(r7, r0)
            L_0x0272:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SessionsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == SessionsActivity.this.terminateAllSessionsRow || i == SessionsActivity.this.qrCodeRow) {
                return 0;
            }
            if (i == SessionsActivity.this.terminateAllSessionsDetailRow || i == SessionsActivity.this.otherSessionsTerminateDetail || i == SessionsActivity.this.passwordSessionsDetailRow) {
                return 1;
            }
            if (i == SessionsActivity.this.currentSessionSectionRow || i == SessionsActivity.this.otherSessionsSectionRow || i == SessionsActivity.this.passwordSessionsSectionRow) {
                return 2;
            }
            if (i == SessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (i == SessionsActivity.this.currentSessionRow) {
                return 4;
            }
            if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (i < SessionsActivity.this.passwordSessionsStartRow || i >= SessionsActivity.this.passwordSessionsEndRow) {
                return 0;
            }
            return 4;
        }
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
        arrayList.add(new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sessions_devicesImage"));
        arrayList.add(new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
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
}
