package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_channels_setStickers;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int chatId;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public boolean donePressed;
    private EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public ImageView eraseImageView;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public LinearLayout nameContainer;
    /* access modifiers changed from: private */
    public int nameRow;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private Runnable queryRunnable;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    private boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectedStickerRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_messages_stickerSet selectedStickerSet;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;

    public GroupStickersActivity(int i) {
        this.chatId = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public View createView(Context context) {
        TLRPC$StickerSet tLRPC$StickerSet;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupStickersActivity.this.finishFragment();
                } else if (i == 1 && !GroupStickersActivity.this.donePressed) {
                    boolean unused = GroupStickersActivity.this.donePressed = true;
                    if (GroupStickersActivity.this.searching) {
                        GroupStickersActivity.this.showEditDoneProgress(true);
                    } else {
                        GroupStickersActivity.this.saveStickerSet();
                    }
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r2 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                }
            }
        };
        this.nameContainer = r2;
        r2.setWeightSum(1.0f);
        this.nameContainer.setWillNotDraw(false);
        this.nameContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.nameContainer.setOrientation(0);
        this.nameContainer.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(14.0f), 0);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/");
        this.editText.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setFocusable(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setGravity(16);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.nameContainer.addView(this.editText, LayoutHelper.createLinear(-2, 42));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.usernameTextView = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.usernameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable((Drawable) null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(16);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChooseStickerSetPlaceholder", NUM));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            boolean ignoreTextChange;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (GroupStickersActivity.this.eraseImageView != null) {
                    GroupStickersActivity.this.eraseImageView.setVisibility(editable.length() > 0 ? 0 : 4);
                }
                if (!this.ignoreTextChange && !GroupStickersActivity.this.ignoreTextChanges) {
                    if (editable.length() > 5) {
                        this.ignoreTextChange = true;
                        try {
                            Uri parse = Uri.parse(editable.toString());
                            if (parse != null) {
                                List<String> pathSegments = parse.getPathSegments();
                                if (pathSegments.size() == 2 && pathSegments.get(0).toLowerCase().equals("addstickers")) {
                                    GroupStickersActivity.this.usernameTextView.setText(pathSegments.get(1));
                                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                                }
                            }
                        } catch (Exception unused) {
                        }
                        this.ignoreTextChange = false;
                    }
                    GroupStickersActivity.this.resolveStickerSet();
                }
            }
        });
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        ImageView imageView = new ImageView(context2);
        this.eraseImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseImageView.setImageResource(NUM);
        this.eraseImageView.setPadding(AndroidUtilities.dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), PorterDuff.Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupStickersActivity.this.lambda$createView$0$GroupStickersActivity(view);
            }
        });
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (!(tLRPC$ChatFull == null || (tLRPC$StickerSet = tLRPC$ChatFull.stickerset) == null)) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(tLRPC$StickerSet.short_name);
            EditTextBoldCursor editTextBoldCursor3 = this.usernameTextView;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
            this.ignoreTextChanges = false;
        }
        this.listAdapter = new ListAdapter(context2);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass4 r4 = new LinearLayoutManager(this, context2) {
            public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z, boolean z2) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r4;
        r4.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupStickersActivity.this.lambda$createView$1$GroupStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$GroupStickersActivity(View view) {
        this.searchWas = false;
        this.selectedStickerSet = null;
        this.usernameTextView.setText("");
        updateRows();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$GroupStickersActivity(View view, int i) {
        if (getParentActivity() != null) {
            int i2 = this.selectedStickerRow;
            if (i == i2) {
                if (this.selectedStickerSet != null) {
                    showDialog(new StickersAlert(getParentActivity(), this, (TLRPC$InputStickerSet) null, this.selectedStickerSet, (StickersAlert.StickersAlertDelegate) null));
                }
            } else if (i >= this.stickersStartRow && i < this.stickersEndRow) {
                boolean z = i2 == -1;
                int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                int top = holder != null ? holder.itemView.getTop() : Integer.MAX_VALUE;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(i - this.stickersStartRow);
                this.selectedStickerSet = tLRPC$TL_messages_stickerSet;
                this.ignoreTextChanges = true;
                this.usernameTextView.setText(tLRPC$TL_messages_stickerSet.set.short_name);
                EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreTextChanges = false;
                AndroidUtilities.hideKeyboard(this.usernameTextView);
                updateRows();
                if (z && top != Integer.MAX_VALUE) {
                    this.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition + 1, top);
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 0) {
                updateRows();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.chatId) {
                if (this.info == null && tLRPC$ChatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(tLRPC$ChatFull.stickerset);
                }
                this.info = tLRPC$ChatFull;
                updateRows();
            }
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            objArr[0].longValue();
            TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
            if (tLRPC$ChatFull2 != null && (tLRPC$StickerSet = tLRPC$ChatFull2.stickerset) != null && tLRPC$StickerSet.id == ((long) i)) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    /* access modifiers changed from: private */
    public void resolveStickerSet() {
        if (this.listAdapter != null) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.queryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.queryRunnable = null;
            }
            this.selectedStickerSet = null;
            if (this.usernameTextView.length() <= 0) {
                this.searching = false;
                this.searchWas = false;
                if (this.selectedStickerRow != -1) {
                    updateRows();
                    return;
                }
                return;
            }
            this.searching = true;
            this.searchWas = true;
            String obj = this.usernameTextView.getText().toString();
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(obj);
            if (stickerSetByName != null) {
                this.selectedStickerSet = stickerSetByName;
            }
            int i = this.selectedStickerRow;
            if (i == -1) {
                updateRows();
            } else {
                this.listAdapter.notifyItemChanged(i);
            }
            if (stickerSetByName != null) {
                this.searching = false;
                return;
            }
            $$Lambda$GroupStickersActivity$LE0Hkmgdmlll6CmITp9ssUUozs r1 = new Runnable(obj) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupStickersActivity.this.lambda$resolveStickerSet$4$GroupStickersActivity(this.f$1);
                }
            };
            this.queryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 500);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$resolveStickerSet$4 */
    public /* synthetic */ void lambda$resolveStickerSet$4$GroupStickersActivity(String str) {
        if (this.queryRunnable != null) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
            tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetShortName;
            tLRPC$TL_inputStickerSetShortName.short_name = str;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    GroupStickersActivity.this.lambda$null$3$GroupStickersActivity(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$GroupStickersActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupStickersActivity.this.lambda$null$2$GroupStickersActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$GroupStickersActivity(TLObject tLObject) {
        this.searching = false;
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            this.selectedStickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            if (this.donePressed) {
                saveStickerSet();
            } else {
                int i = this.selectedStickerRow;
                if (i != -1) {
                    this.listAdapter.notifyItemChanged(i);
                } else {
                    updateRows();
                }
            }
        } else {
            int i2 = this.selectedStickerRow;
            if (i2 != -1) {
                this.listAdapter.notifyItemChanged(i2);
            }
            if (this.donePressed) {
                this.donePressed = false;
                showEditDoneProgress(false);
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("AddStickersNotFound", NUM), 0).show();
                }
            }
        }
        this.reqId = 0;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    GroupStickersActivity.this.lambda$onTransitionAnimationEnd$5$GroupStickersActivity();
                }
            }, 100);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTransitionAnimationEnd$5 */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$5$GroupStickersActivity() {
        EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void saveStickerSet() {
        TLRPC$StickerSet tLRPC$StickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null || (!((tLRPC$StickerSet = tLRPC$ChatFull.stickerset) == null || (tLRPC$TL_messages_stickerSet = this.selectedStickerSet) == null || tLRPC$TL_messages_stickerSet.set.id != tLRPC$StickerSet.id) || (tLRPC$StickerSet == null && this.selectedStickerSet == null))) {
            finishFragment();
            return;
        }
        showEditDoneProgress(true);
        TLRPC$TL_channels_setStickers tLRPC$TL_channels_setStickers = new TLRPC$TL_channels_setStickers();
        tLRPC$TL_channels_setStickers.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        if (this.selectedStickerSet == null) {
            tLRPC$TL_channels_setStickers.stickerset = new TLRPC$TL_inputStickerSetEmpty();
        } else {
            SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
            edit.remove("group_hide_stickers_" + this.info.id).commit();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_channels_setStickers.stickerset = tLRPC$TL_inputStickerSetID;
            TLRPC$StickerSet tLRPC$StickerSet2 = this.selectedStickerSet.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_setStickers, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupStickersActivity.this.lambda$saveStickerSet$7$GroupStickersActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$saveStickerSet$7 */
    public /* synthetic */ void lambda$saveStickerSet$7$GroupStickersActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupStickersActivity.this.lambda$null$6$GroupStickersActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$6 */
    public /* synthetic */ void lambda$null$6$GroupStickersActivity(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.selectedStickerSet;
            if (tLRPC$TL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tLRPC$TL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull.stickerset == null) {
                tLRPC$ChatFull.flags |= 256;
            } else {
                tLRPC$ChatFull.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, Boolean.TRUE, Boolean.FALSE);
            finishFragment();
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text, 0).show();
        this.donePressed = false;
        showEditDoneProgress(false);
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.nameRow = 0;
        if (this.selectedStickerSet != null || this.searchWas) {
            this.rowCount = i + 1;
            this.selectedStickerRow = i;
        } else {
            this.selectedStickerRow = -1;
        }
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.infoRow = i2;
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (!stickerSets.isEmpty()) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.headerRow = i3;
            this.stickersStartRow = i4;
            this.stickersEndRow = i4 + stickerSets.size();
            int size = this.rowCount + stickerSets.size();
            this.rowCount = size;
            this.rowCount = size + 1;
            this.stickersShadowRow = size;
        } else {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        }
        LinearLayout linearLayout = this.nameContainer;
        if (linearLayout != null) {
            linearLayout.invalidate();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.usernameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean z) {
        final boolean z2 = z;
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z2) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z2) {
                            GroupStickersActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupStickersActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = GroupStickersActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                int access$1400 = i - GroupStickersActivity.this.stickersStartRow;
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(access$1400);
                stickerSetCell.setStickersSet(stickerSets.get(access$1400), access$1400 != stickerSets.size() - 1);
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    j = GroupStickersActivity.this.selectedStickerSet.set.id;
                } else {
                    j = (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0 : GroupStickersActivity.this.info.stickerset.id;
                }
                if (tLRPC$TL_messages_stickerSet.set.id != j) {
                    z = false;
                }
                stickerSetCell.setChecked(z);
            } else if (itemViewType != 1) {
                if (itemViewType == 4) {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", NUM));
                } else if (itemViewType == 5) {
                    StickerSetCell stickerSetCell2 = (StickerSetCell) viewHolder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        stickerSetCell2.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                    } else if (GroupStickersActivity.this.searching) {
                        stickerSetCell2.setText(LocaleController.getString("Loading", NUM), (String) null, 0, false);
                    } else {
                        stickerSetCell2.setText(LocaleController.getString("ChooseStickerSetNotFound", NUM), LocaleController.getString("ChooseStickerSetNotFoundInfo", NUM), NUM, false);
                    }
                }
            } else if (i == GroupStickersActivity.this.infoRow) {
                String string = LocaleController.getString("ChooseStickerSetMy", NUM);
                int indexOf = string.indexOf("@stickers");
                if (indexOf != -1) {
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                            public void onClick(View view) {
                                MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                            }
                        }, indexOf, indexOf + 9, 18);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                    }
                } else {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2 || itemViewType == 5;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            int i2 = 3;
            if (i != 0) {
                if (i == 1) {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == 2) {
                    view = GroupStickersActivity.this.nameContainer;
                } else if (i == 3) {
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == 4) {
                    HeaderCell headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                } else if (i != 5) {
                    view = null;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }
            Context context = this.mContext;
            if (i != 0) {
                i2 = 2;
            }
            StickerSetCell stickerSetCell = new StickerSetCell(context, i2);
            stickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            view = stickerSetCell;
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            if (i == GroupStickersActivity.this.nameRow) {
                return 2;
            }
            if (i == GroupStickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.selectedStickerRow) {
                return 5;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.nameContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        return arrayList;
    }
}
