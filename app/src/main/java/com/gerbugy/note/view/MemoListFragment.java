package com.gerbugy.note.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.gerbugy.note.Constants;
import com.gerbugy.note.R;
import com.gerbugy.note.databinding.MemoListBinding;
import com.gerbugy.note.databinding.MemoListItemBinding;
import com.gerbugy.note.db.MemoDao;
import com.gerbugy.note.db.SQLiteItem;
import com.gerbugy.note.util.DateUtils;
import com.gerbugy.note.util.SQLiteItemUtils;
import com.gerbugy.note.widget.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoListFragment extends MainFragment {

    private final Handler mHandler = new Handler();
    private final List<SQLiteItem> mItems = new ArrayList<>();
    private final RecyclerViewAdapter mAdapter = new RecyclerViewAdapter();

    private MemoListBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasFloatingActionButton(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (mBinding = DataBindingUtil.inflate(inflater, R.layout.memo_list, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter, false, true)).attachToRecyclerView(mBinding.recyclerView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.memo_list, menu);
        initSearchMenu(menu);
    }

    private void initSearchMenu(Menu menu) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // searchView.setFocusable(false);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI); // 가로모드에서 입력란이 전체를 차지하지 않도록 합니다.
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                select(null);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searchView.clearFocus();
                select(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mItems.isEmpty()) {
            select(null);
        }
    }

    private void select(String query) {
        mItems.clear();
        mItems.addAll(MemoDao.getInstance().selectList(TextUtils.isEmpty(query) ? null : " content LIKE '%" + query + "%'", "position DESC, created_at DESC", null));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.RESULT_INSERTED:
                long insertedId = data.getLongExtra(MemoDao.Columns._ID, Constants.NO_ID);
                int insertedPosition = 0;
                mItems.add(insertedPosition, MemoDao.getInstance().select(insertedId));
                mAdapter.notifyItemInserted(insertedPosition);
                mBinding.recyclerView.smoothScrollToPosition(insertedPosition);
                break;
            case Constants.RESULT_CHANGED:
                long changedId = data.getLongExtra(MemoDao.Columns._ID, Constants.NO_ID);
                int changedPosition = SQLiteItemUtils.findPosition(mItems, changedId);
                mItems.set(changedPosition, MemoDao.getInstance().select(changedId));
                mAdapter.notifyItemChanged(changedPosition);
                break;
            case Constants.RESULT_REMOVED:
                long removedId = data.getLongExtra(MemoDao.Columns._ID, Constants.NO_ID);
                int removedPosition = SQLiteItemUtils.findPosition(mItems, removedId);
                mItems.remove(removedPosition);
                mAdapter.notifyItemRemoved(removedPosition);
                break;
        }
    }

    @Override
    public void onFloatingActionButtonClick(View v) {
        startActivityForResult(new Intent(getContext(), MemoEditActivity.class), Constants.REQUEST_INSERT);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperCallback.Adapter {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(MemoListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SQLiteItem item = mItems.get(position);
            holder.binding.content.setText(item.getString(MemoDao.Columns.CONTENT));
            holder.binding.important.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), item.getInt(MemoDao.Columns.IMPORTANT) == 0 ? R.color.gray : R.color.colorAccent));
            holder.binding.createdAt.setText(DateUtils.formatSameDayTime(item.getLong(MemoDao.Columns.CREATED_AT)));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public boolean onItemMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            Collections.swap(mItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SQLiteItem fromItem = mItems.get(fromPosition);
                    SQLiteItem toItem = mItems.get(toPosition);
                    int fromPosition = fromItem.getInt(MemoDao.Columns.POSITION);
                    fromItem.put(MemoDao.Columns.POSITION, toItem.getInt(MemoDao.Columns.POSITION));
                    toItem.put(MemoDao.Columns.POSITION, fromPosition);
                    MemoDao.getInstance().updatePosition(fromItem, toItem);
                }
            });
            return true;
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            MemoListItemBinding binding;

            ViewHolder(MemoListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(this);
                binding.important.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                final SQLiteItem item = mItems.get(getAdapterPosition());
                switch (v.getId()) {
                    case R.id.important:
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                int important = item.getInt(MemoDao.Columns.IMPORTANT);
                                item.put(MemoDao.Columns.IMPORTANT, important == 0 ? 1 : 0);
                                MemoDao.getInstance().update(item);
                                notifyItemChanged(getAdapterPosition());
                            }
                        });
                        break;
                    default:
                        Intent intent = new Intent(getContext(), MemoViewActivity.class);
                        intent.putExtra(MemoDao.Columns._ID, item.getLong(MemoDao.Columns._ID));
                        startActivityForResult(intent, Constants.REQUEST_VIEW);
                        break;
                }
            }
        }
    }
}
