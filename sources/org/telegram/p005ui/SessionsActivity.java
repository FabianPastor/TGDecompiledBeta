package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.SessionCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_getWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_webAuthorizations;
import org.telegram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.SessionsActivity */
public class SessionsActivity extends BaseFragment implements NotificationCenterDelegate {
    private TL_authorization currentSession;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private int currentType;
    private LinearLayout emptyLayout;
    private EmptyTextProgressView emptyView;
    private ImageView imageView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int rowCount;
    private ArrayList<TLObject> sessions = new ArrayList();
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;
    private TextView textView1;
    private TextView textView2;

    /* renamed from: org.telegram.ui.SessionsActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                SessionsActivity.this.lambda$checkDiscard$69$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$2 */
    class CLASSNAME implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SessionsActivity$2$1 */
        class CLASSNAME implements OnClickListener {

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$1 */
            class CLASSNAME implements RequestDelegate {
                CLASSNAME() {
                }

                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (error == null && (response instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", CLASSNAMER.string.TerminateAllSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", CLASSNAMER.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.lambda$checkDiscard$69$PassportActivity();
                            }
                        }
                    });
                    for (int a = 0; a < 3; a++) {
                        UserConfig userConfig = UserConfig.getInstance(a);
                        if (userConfig.isClientActivated()) {
                            userConfig.registeredForPush = false;
                            userConfig.saveConfig(false);
                            MessagesController.getInstance(a).registerForPush(SharedConfig.pushString);
                            ConnectionsManager.getInstance(a).setUserId(userConfig.getClientUserId());
                        }
                    }
                }
            }

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$2 */
            class CLASSNAME implements RequestDelegate {
                CLASSNAME() {
                }

                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (error == null && (response instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllWebSessions", CLASSNAMER.string.TerminateAllWebSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", CLASSNAMER.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.lambda$checkDiscard$69$PassportActivity();
                            }
                        }
                    });
                }
            }

            CLASSNAME() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (SessionsActivity.this.currentType == 0) {
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_auth_resetAuthorizations(), new CLASSNAME());
                    return;
                }
                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_account_resetWebAuthorizations(), new CLASSNAME());
            }
        }

        CLASSNAME() {
        }

        public void onItemClick(View view, int position) {
            Builder builder;
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                if (SessionsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SessionsActivity.this.getParentActivity());
                    if (SessionsActivity.this.currentType == 0) {
                        builder.setMessage(LocaleController.getString("AreYouSureSessions", CLASSNAMER.string.AreYouSureSessions));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureWebSessions", CLASSNAMER.string.AreYouSureWebSessions));
                    }
                    builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new CLASSNAME());
                    builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                    SessionsActivity.this.showDialog(builder.create());
                }
            } else if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow && SessionsActivity.this.getParentActivity() != null) {
                builder = new Builder(SessionsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                final boolean[] param = new boolean[1];
                if (SessionsActivity.this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("TerminateSessionQuestion", CLASSNAMER.string.TerminateSessionQuestion));
                } else {
                    String name;
                    builder.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", CLASSNAMER.string.TerminateWebSessionQuestion, ((TL_webAuthorization) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow)).domain));
                    FrameLayout frameLayout = new FrameLayout(SessionsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SessionsActivity.this.currentAccount).getUser(Integer.valueOf(authorization.bot_id));
                    if (user != null) {
                        name = UserObject.getFirstName(user);
                    } else {
                        name = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    CheckBoxCell cell = new CheckBoxCell(SessionsActivity.this.getParentActivity(), 1);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setText(LocaleController.formatString("TerminateWebSessionStop", CLASSNAMER.string.TerminateWebSessionStop, name), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.m10dp(16.0f) : AndroidUtilities.m10dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.m10dp(8.0f) : AndroidUtilities.m10dp(16.0f), 0);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (v.isEnabled()) {
                                boolean z;
                                CheckBoxCell cell = (CheckBoxCell) v;
                                boolean[] zArr = param;
                                if (param[0]) {
                                    z = false;
                                } else {
                                    z = true;
                                }
                                zArr[0] = z;
                                cell.setChecked(param[0], true);
                            }
                        }
                    });
                    builder.setCustomViewOffset(16);
                    builder.setView(frameLayout);
                }
                final int i = position;
                builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int option) {
                        if (SessionsActivity.this.getParentActivity() != null) {
                            final AlertDialog progressDialog = new AlertDialog(SessionsActivity.this.getParentActivity(), 1);
                            progressDialog.setMessage(LocaleController.getString("Loading", CLASSNAMER.string.Loading));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            if (SessionsActivity.this.currentType == 0) {
                                final TL_authorization authorization = (TL_authorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                                TL_account_resetAuthorization req = new TL_account_resetAuthorization();
                                req.hash = authorization.hash;
                                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public void run(TLObject response, final TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    progressDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m14e(e);
                                                }
                                                if (error == null) {
                                                    SessionsActivity.this.sessions.remove(authorization);
                                                    SessionsActivity.this.updateRows();
                                                    if (SessionsActivity.this.listAdapter != null) {
                                                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                return;
                            }
                            final TL_webAuthorization authorization2 = (TL_webAuthorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                            TL_account_resetWebAuthorization req2 = new TL_account_resetWebAuthorization();
                            req2.hash = authorization2.hash;
                            ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() {
                                public void run(TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            try {
                                                progressDialog.dismiss();
                                            } catch (Throwable e) {
                                                FileLog.m14e(e);
                                            }
                                            if (error == null) {
                                                SessionsActivity.this.sessions.remove(authorization2);
                                                SessionsActivity.this.updateRows();
                                                if (SessionsActivity.this.listAdapter != null) {
                                                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                            if (param[0]) {
                                MessagesController.getInstance(SessionsActivity.this.currentAccount).blockUser(authorization2.bot_id);
                            }
                        }
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                SessionsActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$3 */
    class CLASSNAME implements RequestDelegate {
        CLASSNAME() {
        }

        public void run(final TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (error == null) {
                        SessionsActivity.this.sessions.clear();
                        Iterator it = response.authorizations.iterator();
                        while (it.hasNext()) {
                            TL_authorization authorization = (TL_authorization) it.next();
                            if ((authorization.flags & 1) != 0) {
                                SessionsActivity.this.currentSession = authorization;
                            } else {
                                SessionsActivity.this.sessions.add(authorization);
                            }
                        }
                        SessionsActivity.this.updateRows();
                    }
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$4 */
    class CLASSNAME implements RequestDelegate {
        CLASSNAME() {
        }

        public void run(final TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (error == null) {
                        SessionsActivity.this.sessions.clear();
                        TL_account_webAuthorizations res = response;
                        MessagesController.getInstance(SessionsActivity.this.currentAccount).putUsers(res.users, false);
                        SessionsActivity.this.sessions.addAll(res.authorizations);
                        SessionsActivity.this.updateRows();
                    }
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SessionsActivity.this.terminateAllSessionsRow || (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow);
        }

        public int getItemCount() {
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
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
                case 3:
                    view = SessionsActivity.this.emptyLayout;
                    break;
                default:
                    view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            boolean z2 = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
                        if (SessionsActivity.this.currentType == 0) {
                            textCell.setText(LocaleController.getString("TerminateAllSessions", CLASSNAMER.string.TerminateAllSessions), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("TerminateAllWebSessions", CLASSNAMER.string.TerminateAllWebSessions), false);
                            return;
                        }
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", CLASSNAMER.string.ClearOtherSessionsHelp));
                        } else {
                            privacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", CLASSNAMER.string.ClearOtherWebSessionsHelp));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("TerminateSessionInfo", CLASSNAMER.string.TerminateSessionInfo));
                        } else {
                            privacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", CLASSNAMER.string.TerminateWebSessionInfo));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == SessionsActivity.this.currentSessionSectionRow) {
                        headerCell.setText(LocaleController.getString("CurrentSession", CLASSNAMER.string.CurrentSession));
                        return;
                    } else if (position != SessionsActivity.this.otherSessionsSectionRow) {
                        return;
                    } else {
                        if (SessionsActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("OtherSessions", CLASSNAMER.string.OtherSessions));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("OtherWebSessions", CLASSNAMER.string.OtherWebSessions));
                            return;
                        }
                    }
                case 3:
                    LayoutParams layoutParams = SessionsActivity.this.emptyLayout.getLayoutParams();
                    if (layoutParams != null) {
                        int i;
                        int dp = AndroidUtilities.m10dp(220.0f);
                        int currentActionBarHeight = (AndroidUtilities.displaySize.y - CLASSNAMEActionBar.getCurrentActionBarHeight()) - AndroidUtilities.m10dp(128.0f);
                        if (VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        } else {
                            i = 0;
                        }
                        layoutParams.height = Math.max(dp, currentActionBarHeight - i);
                        SessionsActivity.this.emptyLayout.setLayoutParams(layoutParams);
                        return;
                    }
                    return;
                default:
                    SessionCell sessionCell = holder.itemView;
                    TLObject access$1400;
                    if (position == SessionsActivity.this.currentSessionRow) {
                        access$1400 = SessionsActivity.this.currentSession;
                        if (!SessionsActivity.this.sessions.isEmpty()) {
                            z2 = true;
                        }
                        sessionCell.setSession(access$1400, z2);
                        return;
                    }
                    access$1400 = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                    if (position == SessionsActivity.this.otherSessionsEndRow - 1) {
                        z = false;
                    }
                    sessionCell.setSession(access$1400, z);
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position == SessionsActivity.this.terminateAllSessionsDetailRow || position == SessionsActivity.this.otherSessionsTerminateDetail) {
                return 1;
            }
            if (position == SessionsActivity.this.currentSessionSectionRow || position == SessionsActivity.this.otherSessionsSectionRow) {
                return 2;
            }
            if (position == SessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (position == SessionsActivity.this.currentSessionRow || (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow)) {
                return 4;
            }
            return 0;
        }
    }

    public SessionsActivity(int type) {
        this.currentType = type;
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
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("SessionsTitle", CLASSNAMER.string.SessionsTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", CLASSNAMER.string.WebSessionsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - CLASSNAMEActionBar.getCurrentActionBarHeight()));
        this.imageView = new ImageView(context);
        if (this.currentType == 0) {
            this.imageView.setImageResource(CLASSNAMER.drawable.devices);
        } else {
            this.imageView.setImageResource(CLASSNAMER.drawable.no_apps);
        }
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), Mode.MULTIPLY));
        this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
        this.textView1 = new TextView(context);
        this.textView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView1.setGravity(17);
        this.textView1.setTextSize(1, 17.0f);
        this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (this.currentType == 0) {
            this.textView1.setText(LocaleController.getString("NoOtherSessions", CLASSNAMER.string.NoOtherSessions));
        } else {
            this.textView1.setText(LocaleController.getString("NoOtherWebSessions", CLASSNAMER.string.NoOtherWebSessions));
        }
        this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        this.textView2 = new TextView(context);
        this.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView2.setGravity(17);
        this.textView2.setTextSize(1, 17.0f);
        this.textView2.setPadding(AndroidUtilities.m10dp(20.0f), 0, AndroidUtilities.m10dp(20.0f), 0);
        if (this.currentType == 0) {
            this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", CLASSNAMER.string.NoOtherSessionsInfo));
        } else {
            this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", CLASSNAMER.string.NoOtherWebSessionsInfo));
        }
        this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new CLASSNAME());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            loadSessions(true);
        }
    }

    private void loadSessions(boolean silent) {
        if (!this.loading) {
            if (!silent) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAuthorizations(), new CLASSNAME()), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getWebAuthorizations(), new CLASSNAME()), this.classGuid);
        }
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.currentSession != null) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (this.sessions.isEmpty()) {
            if (this.currentType == 1 || this.currentSession != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.noOtherSessionsRow = i;
            } else {
                this.noOtherSessionsRow = -1;
            }
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
        this.noOtherSessionsRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsSectionRow = i;
        this.otherSessionsStartRow = this.otherSessionsSectionRow + 1;
        this.otherSessionsEndRow = this.otherSessionsStartRow + this.sessions.size();
        this.rowCount += this.sessions.size();
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsTerminateDetail = i;
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[22];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage);
        r9[10] = new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[11] = new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText2);
        r9[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r9;
    }
}
