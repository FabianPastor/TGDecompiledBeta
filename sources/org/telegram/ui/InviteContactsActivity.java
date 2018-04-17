package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.InviteTextCell;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class InviteContactsActivity extends BaseFragment implements OnClickListener, NotificationCenterDelegate {
    private InviteAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int containerHeight;
    private TextView counterTextView;
    private FrameLayout counterView;
    private GroupCreateSpan currentDeletingSpan;
    private GroupCreateDividerItemDecoration decoration;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private TextView infoTextView;
    private RecyclerListView listView;
    private ArrayList<Contact> phoneBookContacts;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private HashMap<String, GroupCreateSpan> selectedContacts = new HashMap();
    private SpansContainer spansContainer;
    private TextView textView;

    /* renamed from: org.telegram.ui.InviteContactsActivity$5 */
    class C14365 implements Callback {
        C14365() {
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$6 */
    class C14376 implements OnKeyListener {
        private boolean wasEmpty;

        C14376() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean z = true;
            if (event.getAction() == 0) {
                if (InviteContactsActivity.this.editText.length() != 0) {
                    z = false;
                }
                this.wasEmpty = z;
            } else if (event.getAction() == 1 && this.wasEmpty && !InviteContactsActivity.this.allSpans.isEmpty()) {
                InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                InviteContactsActivity.this.updateHint();
                InviteContactsActivity.this.checkVisibleRows();
                return true;
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$7 */
    class C14387 implements TextWatcher {
        C14387() {
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (InviteContactsActivity.this.editText.length() != 0) {
                InviteContactsActivity.this.searching = true;
                InviteContactsActivity.this.searchWas = true;
                InviteContactsActivity.this.adapter.setSearching(true);
                InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                InviteContactsActivity.this.listView.setFastScrollVisible(false);
                InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                InviteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                return;
            }
            InviteContactsActivity.this.closeSearch();
        }
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        /* renamed from: org.telegram.ui.InviteContactsActivity$SpansContainer$1 */
        class C14431 extends AnimatorListenerAdapter {
            C14431() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                InviteContactsActivity.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            SpansContainer spansContainer = this;
            int count = getChildCount();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(32.0f);
            float f = 12.0f;
            int y = AndroidUtilities.dp(12.0f);
            int allY = AndroidUtilities.dp(12.0f);
            int allCurrentLineWidth = 0;
            int y2 = y;
            y = 0;
            int a = 0;
            while (a < count) {
                int x;
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (child != spansContainer.removingSpan && child.getMeasuredWidth() + y > maxWidth) {
                        y2 += child.getMeasuredHeight() + AndroidUtilities.dp(f);
                        y = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(f);
                        allCurrentLineWidth = 0;
                    }
                    x = AndroidUtilities.dp(16.0f) + y;
                    if (!spansContainer.animationStarted) {
                        if (child == spansContainer.removingSpan) {
                            child.setTranslationX((float) (AndroidUtilities.dp(16.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (spansContainer.removingSpan != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y2)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y2}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y2);
                        }
                    }
                    if (child != spansContainer.removingSpan) {
                        y += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                a++;
                f = 12.0f;
            }
            if (AndroidUtilities.isTablet()) {
                a = AndroidUtilities.dp(366.0f) / 3;
            } else {
                a = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (maxWidth - y < a) {
                y = 0;
                y2 += AndroidUtilities.dp(44.0f);
            }
            if (maxWidth - allCurrentLineWidth < a) {
                allY += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(maxWidth - y, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            int i;
            if (spansContainer.animationStarted) {
                i = maxWidth;
                if (!(spansContainer.currentAnimation == null || InviteContactsActivity.this.ignoreScrollEvent || spansContainer.removingSpan != null)) {
                    InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
                }
            } else {
                int currentHeight = AndroidUtilities.dp(44.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + y;
                InviteContactsActivity.this.fieldY = y2;
                if (spansContainer.currentAnimation != null) {
                    boolean z;
                    if (InviteContactsActivity.this.containerHeight != AndroidUtilities.dp(44.0f) + y2) {
                        spansContainer.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", new int[]{x}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        spansContainer.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != ((float) InviteContactsActivity.this.fieldY)) {
                        ArrayList arrayList = spansContainer.animators;
                        float[] fArr = new float[1];
                        z = false;
                        fArr[0] = (float) InviteContactsActivity.this.fieldY;
                        arrayList.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationY", fArr));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    spansContainer.currentAnimation.playTogether(spansContainer.animators);
                    spansContainer.currentAnimation.start();
                    spansContainer.animationStarted = true;
                } else {
                    i = maxWidth;
                    InviteContactsActivity.this.containerHeight = currentHeight;
                    InviteContactsActivity.this.editText.setTranslationX((float) fieldX);
                    InviteContactsActivity.this.editText.setTranslationY((float) InviteContactsActivity.this.fieldY);
                }
            }
            setMeasuredDimension(width, InviteContactsActivity.this.containerHeight);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            InviteContactsActivity.this.allSpans.add(span);
            InviteContactsActivity.this.selectedContacts.put(span.getKey(), span);
            InviteContactsActivity.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new C14431());
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(span.getKey());
            InviteContactsActivity.this.allSpans.remove(span);
            span.setOnClickListener(null);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty()) {
                        InviteContactsActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$1 */
    class C21571 extends ActionBarMenuOnItemClick {
        C21571() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                InviteContactsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$8 */
    class C21598 implements OnItemClickListener {
        C21598() {
        }

        public void onItemClick(View view, int position) {
            boolean z = false;
            if (position == 0 && !InviteContactsActivity.this.searching) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    String text = ContactsController.getInstance(InviteContactsActivity.this.currentAccount).getInviteText(0);
                    intent.putExtra("android.intent.extra.TEXT", text);
                    InviteContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, text), 500);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if (view instanceof InviteUserCell) {
                InviteUserCell cell = (InviteUserCell) view;
                Contact contact = cell.getContact();
                if (contact != null) {
                    boolean containsKey = InviteContactsActivity.this.selectedContacts.containsKey(contact.key);
                    boolean exists = containsKey;
                    if (containsKey) {
                        InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.selectedContacts.get(contact.key));
                    } else {
                        GroupCreateSpan span = new GroupCreateSpan(InviteContactsActivity.this.editText.getContext(), contact);
                        InviteContactsActivity.this.spansContainer.addSpan(span);
                        span.setOnClickListener(InviteContactsActivity.this);
                    }
                    InviteContactsActivity.this.updateHint();
                    if (!InviteContactsActivity.this.searching) {
                        if (!InviteContactsActivity.this.searchWas) {
                            if (!exists) {
                                z = true;
                            }
                            cell.setChecked(z, true);
                            if (InviteContactsActivity.this.editText.length() > 0) {
                                InviteContactsActivity.this.editText.setText(null);
                            }
                        }
                    }
                    AndroidUtilities.showKeyboard(InviteContactsActivity.this.editText);
                    if (InviteContactsActivity.this.editText.length() > 0) {
                        InviteContactsActivity.this.editText.setText(null);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.InviteContactsActivity$9 */
    class C21609 extends OnScrollListener {
        C21609() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
            }
        }
    }

    public class InviteAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<Contact> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public InviteAdapter(Context ctx) {
            this.context = ctx;
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            if (this.searching) {
                return this.searchResult.size();
            }
            return InviteContactsActivity.this.phoneBookContacts.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 1) {
                view = new InviteUserCell(this.context, true);
            } else {
                view = new InviteTextCell(this.context);
                ((InviteTextCell) view).setTextAndIcon(LocaleController.getString("ShareTelegram", R.string.ShareTelegram), R.drawable.share);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                Contact contact;
                CharSequence name;
                InviteUserCell cell = holder.itemView;
                if (this.searching) {
                    contact = (Contact) this.searchResult.get(position);
                    name = (CharSequence) this.searchResultNames.get(position);
                } else {
                    contact = (Contact) InviteContactsActivity.this.phoneBookContacts.get(position - 1);
                    name = null;
                }
                cell.setUser(contact, name);
                cell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
            }
        }

        public int getItemViewType(int position) {
            if (this.searching || position != 0) {
                return 0;
            }
            return 1;
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof InviteUserCell) {
                ((InviteUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$1 */
                class C14401 implements Runnable {

                    /* renamed from: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$1$1 */
                    class C14391 implements Runnable {
                        C14391() {
                        }

                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                InviteAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            int i = 0;
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<Contact> resultArray = new ArrayList();
                            ArrayList<CharSequence> resultArrayNames = new ArrayList();
                            int a = 0;
                            while (a < InviteContactsActivity.this.phoneBookContacts.size()) {
                                Contact contact = (Contact) InviteContactsActivity.this.phoneBookContacts.get(a);
                                String name = ContactsController.formatName(contact.first_name, contact.last_name).toLowerCase();
                                String tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                int length = search.length;
                                int found = 0;
                                for (int found2 = i; found2 < length; found2++) {
                                    String q = search[found2];
                                    if (!name.startsWith(q)) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(q);
                                        if (!name.contains(stringBuilder.toString())) {
                                            if (tName != null) {
                                                if (!tName.startsWith(q)) {
                                                    StringBuilder stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(" ");
                                                    stringBuilder2.append(q);
                                                }
                                            }
                                            if (found != 0) {
                                                resultArrayNames.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, q));
                                                resultArray.add(contact);
                                                break;
                                            }
                                        }
                                    }
                                    found = 1;
                                    if (found != 0) {
                                        resultArrayNames.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, q));
                                        resultArray.add(contact);
                                        break;
                                    }
                                }
                                a++;
                                i = 0;
                            }
                            InviteAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                        }
                    }

                    C14401() {
                    }

                    public void run() {
                        Utilities.searchQueue.postRunnable(new C14391());
                    }
                }

                public void run() {
                    try {
                        InviteAdapter.this.searchTimer.cancel();
                        InviteAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    AndroidUtilities.runOnUIThread(new C14401());
                }
            }, 200, 300);
        }

        private void updateSearchResults(final ArrayList<Contact> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    InviteAdapter.this.searchResult = users;
                    InviteAdapter.this.searchResultNames = names;
                    InviteAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int count = getItemCount();
            boolean z = false;
            InviteContactsActivity.this.emptyView.setVisibility(count == 1 ? 0 : 4);
            GroupCreateDividerItemDecoration access$3100 = InviteContactsActivity.this.decoration;
            if (count == 1) {
                z = true;
            }
            access$3100.setSingle(z);
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsImported);
        fetchContacts();
        if (!UserConfig.getInstance(this.currentAccount).contactsReimported) {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            UserConfig.getInstance(this.currentAccount).contactsReimported = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteFriends", R.string.InviteFriends));
        this.actionBar.setActionBarMenuOnItemClick(new C21571());
        this.fragmentView = new ViewGroup(context2) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int h;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                if (!AndroidUtilities.isTablet()) {
                    if (height <= width) {
                        maxSize = AndroidUtilities.dp(56.0f);
                        InviteContactsActivity.this.infoTextView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                        InviteContactsActivity.this.counterView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                        if (InviteContactsActivity.this.infoTextView.getVisibility() != 0) {
                            h = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                        } else {
                            h = InviteContactsActivity.this.counterView.getMeasuredHeight();
                        }
                        InviteContactsActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                        InviteContactsActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - h, NUM));
                        InviteContactsActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
                    }
                }
                maxSize = AndroidUtilities.dp(NUM);
                InviteContactsActivity.this.infoTextView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                if (InviteContactsActivity.this.infoTextView.getVisibility() != 0) {
                    h = InviteContactsActivity.this.counterView.getMeasuredHeight();
                } else {
                    h = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                }
                InviteContactsActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - h, NUM));
                InviteContactsActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(72.0f), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                int y = (bottom - top) - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, y, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + y);
                int y2 = (bottom - top) - InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, y2, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + y2);
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == InviteContactsActivity.this.listView || child == InviteContactsActivity.this.emptyView) {
                    InviteContactsActivity.this.parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = this.fragmentView;
        this.scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context2);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    InviteContactsActivity.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        r0.spansContainer.addView(r0.editText);
        r0.editText.setHintText(LocaleController.getString("SearchFriends", R.string.SearchFriends));
        r0.editText.setCustomSelectionActionModeCallback(new C14365());
        r0.editText.setOnKeyListener(new C14376());
        r0.editText.addTextChangedListener(new C14387());
        r0.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(r0.currentAccount).isLoadingContacts()) {
            r0.emptyView.showProgress();
        } else {
            r0.emptyView.showTextView();
        }
        r0.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        frameLayout.addView(r0.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.listView = new RecyclerListView(context2);
        r0.listView.setEmptyView(r0.emptyView);
        RecyclerListView recyclerListView = r0.listView;
        Adapter inviteAdapter = new InviteAdapter(context2);
        r0.adapter = inviteAdapter;
        recyclerListView.setAdapter(inviteAdapter);
        r0.listView.setLayoutManager(linearLayoutManager);
        r0.listView.setVerticalScrollBarEnabled(true);
        r0.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        recyclerListView = r0.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        r0.decoration = groupCreateDividerItemDecoration;
        recyclerListView.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(r0.listView);
        r0.listView.setOnItemClickListener(new C21598());
        r0.listView.setOnScrollListener(new C21609());
        r0.infoTextView = new TextView(context2);
        r0.infoTextView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.infoTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText));
        r0.infoTextView.setGravity(17);
        r0.infoTextView.setText(LocaleController.getString("InviteFriendsHelp", R.string.InviteFriendsHelp));
        r0.infoTextView.setTextSize(1, 13.0f);
        r0.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        frameLayout.addView(r0.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        r0.counterView = new FrameLayout(context2);
        r0.counterView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.counterView.setVisibility(4);
        frameLayout.addView(r0.counterView, LayoutHelper.createFrame(-1, 48, 83));
        r0.counterView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    StringBuilder builder = new StringBuilder();
                    int num = 0;
                    for (int a = 0; a < InviteContactsActivity.this.allSpans.size(); a++) {
                        Contact contact = ((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(a)).getContact();
                        if (builder.length() != 0) {
                            builder.append(';');
                        }
                        builder.append((String) contact.phones.get(0));
                        if (a == 0 && InviteContactsActivity.this.allSpans.size() == 1) {
                            num = contact.imported;
                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("smsto:");
                    stringBuilder.append(builder.toString());
                    Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(stringBuilder.toString()));
                    intent.putExtra("sms_body", ContactsController.getInstance(InviteContactsActivity.this.currentAccount).getInviteText(num));
                    InviteContactsActivity.this.getParentActivity().startActivityForResult(intent, 500);
                    MediaController.getInstance().startSmsObserver();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                InviteContactsActivity.this.finishFragment();
            }
        });
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        r0.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        r0.counterTextView = new TextView(context2);
        r0.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.counterTextView.setTextSize(1, 14.0f);
        r0.counterTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteBackground));
        r0.counterTextView.setGravity(17);
        r0.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), -1));
        r0.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        r0.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(r0.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        r0.textView = new TextView(context2);
        r0.textView.setTextSize(1, 14.0f);
        r0.textView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText));
        r0.textView.setGravity(17);
        r0.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.textView.setText(LocaleController.getString("InviteToTelegram", R.string.InviteToTelegram).toUpperCase());
        r0.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(r0.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        r0.adapter.notifyDataSetChanged();
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsImported) {
            fetchContacts();
        }
    }

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        if (this.spansContainer != null) {
            this.spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof InviteUserCell) {
                InviteUserCell cell = (InviteUserCell) child;
                Contact contact = cell.getContact();
                if (contact != null) {
                    cell.setChecked(this.selectedContacts.containsKey(contact.key), true);
                }
            }
        }
    }

    private void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
            return;
        }
        this.infoTextView.setVisibility(4);
        this.counterView.setVisibility(0);
        this.counterTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())}));
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    private void fetchContacts() {
        this.phoneBookContacts = new ArrayList(ContactsController.getInstance(this.currentAccount).phoneBookContacts);
        Collections.sort(this.phoneBookContacts, new Comparator<Contact>() {
            public int compare(Contact o1, Contact o2) {
                if (o1.imported > o2.imported) {
                    return -1;
                }
                if (o1.imported < o2.imported) {
                    return 1;
                }
                return 0;
            }
        });
        if (this.emptyView != null) {
            this.emptyView.showTextView();
        }
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (InviteContactsActivity.this.listView != null) {
                    int count = InviteContactsActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = InviteContactsActivity.this.listView.getChildAt(a);
                        if (child instanceof InviteUserCell) {
                            ((InviteUserCell) child).update(0);
                        }
                    }
                }
            }
        };
        r9 = new ThemeDescription[44];
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        r9[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        r9[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        r9[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r9[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r9[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkbox);
        r9[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkboxCheck);
        r9[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        r9[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        r9[25] = new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r9[26] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        r9[27] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        r9[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        r9[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        r9[30] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        r9[31] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        r9[32] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        r9[33] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[34] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r9[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundGroupCreateSpanBlue);
        r9[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground);
        r9[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText);
        r9[38] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        r9[39] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteText);
        r9[40] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_contacts_inviteBackground);
        r9[41] = new ThemeDescription(this.counterView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground);
        r9[42] = new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteBackground);
        r9[43] = new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_contacts_inviteText);
        return r9;
    }
}
