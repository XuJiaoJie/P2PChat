package com.xjhaobang.p2pchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.listener.OnClickRecyclerViewListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by PC on 2017/6/3.
 */

public class FriendsListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<SocketBeen> mList;
    private OnClickRecyclerViewListener mRecyclerViewListener;

    public FriendsListAdapter(Context context, List<SocketBeen> list) {
        mContext = context;
        mList = list;
    }

    public void setOnRecyclerViewListener(OnClickRecyclerViewListener onRecyclerViewListener) {
        mRecyclerViewListener = onRecyclerViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friends_list, null);
        return new FriendsListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FriendsListHolder viewHolder = (FriendsListHolder) holder;
        viewHolder.position = position;
        SocketBeen been = mList.get(position);
        if (been.getUserName() == null) {
            viewHolder.mTvUserName.setText("未知");
        } else {
            viewHolder.mTvUserName.setText(been.getUserName());
        }
        if (been.getMsg() == null) {
            if (been.getUserIP() != null) {
                viewHolder.mTvMsg.setText(been.getOtherIP());
            }
        } else {
            viewHolder.mTvMsg.setText(been.getMsg());
        }
        viewHolder.mTvTime.setText(been.getTime());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    class FriendsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.tv_user_name)
        TextView mTvUserName;
        @InjectView(R.id.tv_msg)
        TextView mTvMsg;
        @InjectView(R.id.tv_time)
        TextView mTvTime;
        @InjectView(R.id.rl_item)
        RelativeLayout mRlItem;
        int position;

        FriendsListHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            mRlItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != mRecyclerViewListener) {
                mRecyclerViewListener.onItemClick(position);
            }
        }
    }
}
