package com.example.deardiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder>{

    Context context;
    ArrayList<LiveStreamItem> items;

    public LiveStreamAdapter(Context context, ArrayList<LiveStreamItem> items) {
        this.context = context;
        this.items = items;
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.recycler_live_stream, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LiveStreamAdapter.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;

        LiveStreamItem item = items.get(position);

        //미리보기
        Glide.with(holder.itemView.getContext()).load(item.getPreview()).into(vh.imgPreview);

//        vh.tvAuthor.setText(item.getAuthor());
//        vh.tvTitle.setText(item.getTitle());
        int minute = Integer.parseInt(item.getLength() ) / 60;
        int second = Integer.parseInt(item.getLength() ) % 60;

        if(item.type.equals("live")) { //라이브 방송중인경우
            vh.liveNotification.setVisibility(View.VISIBLE);
            vh.tvAuthor.setVisibility(View.INVISIBLE);
            vh.tvTitle.setText(item.getAuthor() + " 님이 라이브 중입니다.");
        } else {//라이브가 끝난 녹화방송일 경우
            vh.liveNotification.setVisibility(View.INVISIBLE);
            vh.tvAuthor.setVisibility(View.VISIBLE);
            vh.tvAuthor.setText(item.getAuthor());
            if(minute > 0) {
                vh.tvTitle.setText("【"+String.valueOf(minute) + " 분  " + String.valueOf(second) + " 초】" + item.getTitle());
            } else {
                vh.tvTitle.setText("【"+String.valueOf(second) + " 초】" + item.getTitle());
            }
        }

    }

    @Override
    public int getItemCount() {
       return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPreview;
        ImageView liveNotification;
        TextView tvAuthor;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            imgPreview = itemView.findViewById(R.id.imgPreview);
            liveNotification = itemView.findViewById(R.id.live_noti);
            tvAuthor = itemView.findViewById(R.id.streaming_author);
            tvTitle = itemView.findViewById(R.id.streaming_title);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //이벤트가 발생한 아이템의 위치를 알아내기 위한 메소드
                    int pos = getAdapterPosition();

                    /*
                     * notifyDataSetChanged()에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서
                     * 뷰홀더가 참조하는 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는
                     * NO_POSITION을 리턴하기 때문에 리턴값이 NO_POSTION인지 검사해줘야 한다.
                     * */
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }

                }
            });
        }
    }

}






















