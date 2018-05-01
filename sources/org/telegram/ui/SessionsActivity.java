package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizations;
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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    class C22741 extends ActionBarMenuOnItemClick {
        C22741() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SessionsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$2 */
    class C22792 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SessionsActivity$2$1 */
        class C16741 implements OnClickListener {

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$1 */
            class C22751 implements RequestDelegate {
                C22751() {
                }

                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", C0446R.string.TerminateAllSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", C0446R.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.finishFragment();
                            }
                        }
                    });
                    for (tL_error = null; tL_error < 3; tL_error++) {
                        UserConfig instance = UserConfig.getInstance(tL_error);
                        if (instance.isClientActivated()) {
                            instance.registeredForPush = false;
                            instance.saveConfig(false);
                            MessagesController.getInstance(tL_error).registerForPush(SharedConfig.pushString);
                            ConnectionsManager.getInstance(tL_error).setUserId(instance.getClientUserId());
                        }
                    }
                }
            }

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$2 */
            class C22762 implements RequestDelegate {
                C22762() {
                }

                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllWebSessions", C0446R.string.TerminateAllWebSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", C0446R.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.finishFragment();
                            }
                        }
                    });
                }
            }

            C16741() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (SessionsActivity.this.currentType == null) {
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_auth_resetAuthorizations(), new C22751());
                    return;
                }
                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_account_resetWebAuthorizations(), new C22762());
            }
        }

        C22792() {
        }

        public void onItemClick(View view, int i) {
            final int i2 = i;
            if (i2 == SessionsActivity.this.terminateAllSessionsRow) {
                if (SessionsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(SessionsActivity.this.getParentActivity());
                    if (SessionsActivity.this.currentType == 0) {
                        builder.setMessage(LocaleController.getString("AreYouSureSessions", C0446R.string.AreYouSureSessions));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureWebSessions", C0446R.string.AreYouSureWebSessions));
                    }
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16741());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    SessionsActivity.this.showDialog(builder.create());
                }
            } else if (i2 >= SessionsActivity.this.otherSessionsStartRow && i2 < SessionsActivity.this.otherSessionsEndRow && SessionsActivity.this.getParentActivity() != null) {
                Builder builder2 = new Builder(SessionsActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                final boolean[] zArr = new boolean[1];
                if (SessionsActivity.this.currentType == 0) {
                    builder2.setMessage(LocaleController.getString("TerminateSessionQuestion", C0446R.string.TerminateSessionQuestion));
                } else {
                    builder2.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", C0446R.string.TerminateWebSessionQuestion, ((TL_webAuthorization) SessionsActivity.this.sessions.get(i2 - SessionsActivity.this.otherSessionsStartRow)).domain));
                    View frameLayout = new FrameLayout(SessionsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SessionsActivity.this.currentAccount).getUser(Integer.valueOf(r8.bot_id));
                    String firstName = user != null ? UserObject.getFirstName(user) : TtmlNode.ANONYMOUS_REGION_ID;
                    View checkBoxCell = new CheckBoxCell(SessionsActivity.this.getParentActivity(), 1);
                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    checkBoxCell.setText(LocaleController.formatString("TerminateWebSessionStop", C0446R.string.TerminateWebSessionStop, firstName), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    checkBoxCell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (view.isEnabled()) {
                                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                zArr[0] = zArr[0] ^ true;
                                checkBoxCell.setChecked(zArr[0], true);
                            }
                        }
                    });
                    builder2.setCustomViewOffset(16);
                    builder2.setView(frameLayout);
                }
                builder2.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (SessionsActivity.this.getParentActivity() != null) {
                            dialogInterface = new AlertDialog(SessionsActivity.this.getParentActivity(), 1);
                            dialogInterface.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                            dialogInterface.setCanceledOnTouchOutside(false);
                            dialogInterface.setCancelable(false);
                            dialogInterface.show();
                            if (SessionsActivity.this.currentType == 0) {
                                final TL_authorization tL_authorization = (TL_authorization) SessionsActivity.this.sessions.get(i2 - SessionsActivity.this.otherSessionsStartRow);
                                TLObject tL_account_resetAuthorization = new TL_account_resetAuthorization();
                                tL_account_resetAuthorization.hash = tL_authorization.hash;
                                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(tL_account_resetAuthorization, new RequestDelegate() {
                                    public void run(TLObject tLObject, final TL_error tL_error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    dialogInterface.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                                if (tL_error == null) {
                                                    SessionsActivity.this.sessions.remove(tL_authorization);
                                                    SessionsActivity.this.updateRows();
                                                    if (SessionsActivity.this.listAdapter != null) {
                                                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                final TL_webAuthorization tL_webAuthorization = (TL_webAuthorization) SessionsActivity.this.sessions.get(i2 - SessionsActivity.this.otherSessionsStartRow);
                                TLObject tL_account_resetWebAuthorization = new TL_account_resetWebAuthorization();
                                tL_account_resetWebAuthorization.hash = tL_webAuthorization.hash;
                                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(tL_account_resetWebAuthorization, new RequestDelegate() {
                                    public void run(TLObject tLObject, final TL_error tL_error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    dialogInterface.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                                if (tL_error == null) {
                                                    SessionsActivity.this.sessions.remove(tL_webAuthorization);
                                                    SessionsActivity.this.updateRows();
                                                    if (SessionsActivity.this.listAdapter != null) {
                                                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                if (zArr[0] != null) {
                                    MessagesController.getInstance(SessionsActivity.this.currentAccount).blockUser(tL_webAuthorization.bot_id);
                                }
                            }
                        }
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                SessionsActivity.this.showDialog(builder2.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$3 */
    class C22803 implements RequestDelegate {
        C22803() {
        }

        public void run(final TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (tL_error == null) {
                        SessionsActivity.this.sessions.clear();
                        Iterator it = ((TL_account_authorizations) tLObject).authorizations.iterator();
                        while (it.hasNext()) {
                            TL_authorization tL_authorization = (TL_authorization) it.next();
                            if ((tL_authorization.flags & 1) != 0) {
                                SessionsActivity.this.currentSession = tL_authorization;
                            } else {
                                SessionsActivity.this.sessions.add(tL_authorization);
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
    class C22814 implements RequestDelegate {
        C22814() {
        }

        public void run(final TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (tL_error == null) {
                        SessionsActivity.this.sessions.clear();
                        TL_account_webAuthorizations tL_account_webAuthorizations = (TL_account_webAuthorizations) tLObject;
                        MessagesController.getInstance(SessionsActivity.this.currentAccount).putUsers(tL_account_webAuthorizations.users, false);
                        SessionsActivity.this.sessions.addAll(tL_account_webAuthorizations.authorizations);
                        SessionsActivity.this.updateRows();
                    }
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (viewHolder != SessionsActivity.this.terminateAllSessionsRow) {
                if (viewHolder < SessionsActivity.this.otherSessionsStartRow || viewHolder >= SessionsActivity.this.otherSessionsEndRow) {
                    return null;
                }
            }
            return true;
        }

        public int getItemCount() {
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
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
                case 3:
                    viewGroup = SessionsActivity.this.emptyLayout;
                    break;
                default:
                    viewGroup = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == SessionsActivity.this.terminateAllSessionsRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
                        if (SessionsActivity.this.currentType == 0) {
                            textSettingsCell.setText(LocaleController.getString("TerminateAllSessions", C0446R.string.TerminateAllSessions), false);
                            return;
                        } else {
                            textSettingsCell.setText(LocaleController.getString("TerminateAllWebSessions", C0446R.string.TerminateAllWebSessions), false);
                            return;
                        }
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", C0446R.string.ClearOtherSessionsHelp));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", C0446R.string.ClearOtherWebSessionsHelp));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType == 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("TerminateSessionInfo", C0446R.string.TerminateSessionInfo));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", C0446R.string.TerminateWebSessionInfo));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == SessionsActivity.this.currentSessionSectionRow) {
                        headerCell.setText(LocaleController.getString("CurrentSession", C0446R.string.CurrentSession));
                        return;
                    } else if (i != SessionsActivity.this.otherSessionsSectionRow) {
                        return;
                    } else {
                        if (SessionsActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("OtherSessions", C0446R.string.OtherSessions));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("OtherWebSessions", C0446R.string.OtherWebSessions));
                            return;
                        }
                    }
                case 3:
                    viewHolder = SessionsActivity.this.emptyLayout.getLayoutParams();
                    if (viewHolder != null) {
                        int i2;
                        i = AndroidUtilities.dp(NUM);
                        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(128.0f);
                        if (VERSION.SDK_INT >= 21) {
                            i2 = AndroidUtilities.statusBarHeight;
                        }
                        viewHolder.height = Math.max(i, currentActionBarHeight - i2);
                        SessionsActivity.this.emptyLayout.setLayoutParams(viewHolder);
                        return;
                    }
                    return;
                default:
                    SessionCell sessionCell = (SessionCell) viewHolder.itemView;
                    if (i == SessionsActivity.this.currentSessionRow) {
                        sessionCell.setSession(SessionsActivity.this.currentSession, SessionsActivity.this.sessions.isEmpty() ^ true);
                        return;
                    }
                    TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                    if (i != SessionsActivity.this.otherSessionsEndRow - 1) {
                        z = true;
                    }
                    sessionCell.setSession(tLObject, z);
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (i != SessionsActivity.this.terminateAllSessionsDetailRow) {
                if (i != SessionsActivity.this.otherSessionsTerminateDetail) {
                    if (i != SessionsActivity.this.currentSessionSectionRow) {
                        if (i != SessionsActivity.this.otherSessionsSectionRow) {
                            if (i == SessionsActivity.this.noOtherSessionsRow) {
                                return 3;
                            }
                            if (i != SessionsActivity.this.currentSessionRow) {
                                if (i < SessionsActivity.this.otherSessionsStartRow || i >= SessionsActivity.this.otherSessionsEndRow) {
                                    return 0;
                                }
                            }
                            return 4;
                        }
                    }
                    return 2;
                }
            }
            return 1;
        }
    }

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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            r0.actionBar.setTitle(LocaleController.getString("SessionsTitle", C0446R.string.SessionsTitle));
        } else {
            r0.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", C0446R.string.WebSessionsTitle));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22741());
        r0.listAdapter = new ListAdapter(context2);
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) r0.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        r0.emptyLayout = new LinearLayout(context2);
        r0.emptyLayout.setOrientation(1);
        r0.emptyLayout.setGravity(17);
        r0.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        r0.emptyLayout.setLayoutParams(new LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        r0.imageView = new ImageView(context2);
        if (r0.currentType == 0) {
            r0.imageView.setImageResource(C0446R.drawable.devices);
        } else {
            r0.imageView.setImageResource(C0446R.drawable.no_apps);
        }
        r0.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), Mode.MULTIPLY));
        r0.emptyLayout.addView(r0.imageView, LayoutHelper.createLinear(-2, -2));
        r0.textView1 = new TextView(context2);
        r0.textView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.textView1.setGravity(17);
        r0.textView1.setTextSize(1, 17.0f);
        r0.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (r0.currentType == 0) {
            r0.textView1.setText(LocaleController.getString("NoOtherSessions", C0446R.string.NoOtherSessions));
        } else {
            r0.textView1.setText(LocaleController.getString("NoOtherWebSessions", C0446R.string.NoOtherWebSessions));
        }
        r0.emptyLayout.addView(r0.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        r0.textView2 = new TextView(context2);
        r0.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.textView2.setGravity(17);
        r0.textView2.setTextSize(1, 17.0f);
        r0.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (r0.currentType == 0) {
            r0.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", C0446R.string.NoOtherSessionsInfo));
        } else {
            r0.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", C0446R.string.NoOtherWebSessionsInfo));
        }
        r0.emptyLayout.addView(r0.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        r0.emptyView = new EmptyTextProgressView(context2);
        r0.emptyView.showProgress();
        frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        r0.listView = new RecyclerListView(context2);
        r0.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.listView.setEmptyView(r0.emptyView);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setOnItemClickListener(new C22792());
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newSessionReceived) {
            loadSessions(1);
        }
    }

    private void loadSessions(boolean z) {
        if (!this.loading) {
            if (!z) {
                this.loading = true;
            }
            if (this.currentType) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getWebAuthorizations(), new C22814()), this.classGuid);
            } else {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAuthorizations(), new C22803()), this.classGuid);
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentSession != null) {
            int i = this.rowCount;
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
            if (this.currentType != 1) {
                if (this.currentSession == null) {
                    this.noOtherSessionsRow = -1;
                    this.terminateAllSessionsRow = -1;
                    this.terminateAllSessionsDetailRow = -1;
                    this.otherSessionsSectionRow = -1;
                    this.otherSessionsStartRow = -1;
                    this.otherSessionsEndRow = -1;
                    this.otherSessionsTerminateDetail = -1;
                    return;
                }
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.noOtherSessionsRow = i;
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
        r1 = new ThemeDescription[22];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage);
        r1[10] = new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[11] = new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText2);
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r1;
    }
}
