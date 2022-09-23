package com.yunbao.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.main.R;
import com.yunbao.video.bean.VideoBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/26.
 */

public class MainHomeVideoAdapter extends RefreshAdapter<VideoBean> {


    private View.OnClickListener mOnClickListener;
    private int mTotalY;
    private int mLastTotalY;

    public MainHomeVideoAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1) {
            return -1;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return new Vh(mInflater.inflate(R.layout.item_main_home_video_2, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_home_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    /**
     * 删除视频
     */
    public void deleteVideo(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        int position = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (videoId.equals(mList.get(i).getId())) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position, Object payload) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mTotalY += dy;
                if (mLastTotalY != mTotalY && mActionListener != null) {
                    mLastTotalY = mTotalY;
                    mActionListener.onScrollYChanged(-mLastTotalY);
                }
            }
        });
    }

    public ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onScrollYChanged(int scrollY);
    }
}
