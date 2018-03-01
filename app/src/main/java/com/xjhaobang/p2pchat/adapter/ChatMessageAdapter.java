package com.xjhaobang.p2pchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.constant.Constant;

import java.util.List;

/**
 * Created by PC on 2017/6/5.
 */

public class ChatMessageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SocketBeen> mList;

    public ChatMessageAdapter(Context context, List<SocketBeen> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        SocketBeen socketBeen = mList.get(position);
        return socketBeen.getUserIP().equals(Constant.userIP) ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SocketBeen socketBeen = mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            if (!socketBeen.getUserIP().equals(Constant.userIP)){
                convertView = mInflater.inflate(R.layout.main_chat_from_msg, parent, false);
                viewHolder.createDate = (TextView) convertView.findViewById(R.id.chat_from_createDate);
                viewHolder.content = (TextView) convertView.findViewById(R.id.chat_from_content);
                viewHolder.name = (TextView)convertView.findViewById(R.id.chat_from_name);
                convertView.setTag(viewHolder);
            }else {
                convertView = mInflater.inflate(R.layout.main_chat_send_msg, null);
                viewHolder.createDate = (TextView) convertView.findViewById(R.id.chat_send_createDate);
                viewHolder.content = (TextView) convertView.findViewById(R.id.chat_send_content);
                viewHolder.name = (TextView)convertView.findViewById(R.id.chat_send_name);
                convertView.setTag(viewHolder);
            }
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.content.setText(socketBeen.getMsg());
        viewHolder.createDate.setText(socketBeen.getTime());
        viewHolder.name.setText(socketBeen.getUserName());
        return convertView;
    }

    private class ViewHolder{
        public TextView createDate;
        public TextView name;
        public TextView content;
    }

}
