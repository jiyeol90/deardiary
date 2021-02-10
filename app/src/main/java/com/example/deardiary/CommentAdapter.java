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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    ArrayList<CommentItem> items;


    public CommentAdapter(Context context, ArrayList<CommentItem> items) {
        this.context = context;
        this.items = items;
    }

    //커스텀 리스너 인터페이스 정의
    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());

        View itemView=inflater.inflate(R.layout.recycler_comment_item,parent,false);

        ViewHolder viewHolder=new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder vh= (ViewHolder) holder;

        CommentItem item= items.get(position);
        //프로필 사진이 등록되어 있지 않은 유저는 기본 이미지로 세팅해준다.
        if(item.getImgPath().equals("default")) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_profile_default).override(100, 100).into(vh.thumbnail);
        } else {
            Glide.with(holder.itemView.getContext()).load(item.getImgPath()).override(100, 100).into(vh.thumbnail);
        }
        vh.userId.setText(item.getId());
        String userId = UserInfo.getInstance().getId();
        if(!userId.equals(item.getId())) {
            vh.modify.setVisibility(View.INVISIBLE);
        }
        vh.comment.setText(item.getComment());
        vh.dateInfo.setText(item.getDate());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView thumbnail;
        ImageView modify;
        TextView dateInfo;
        TextView userId;
        TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail=itemView.findViewById(R.id.userProfile);
            dateInfo=itemView.findViewById(R.id.comment_date);
            userId = itemView.findViewById(R.id.userId);
            comment = itemView.findViewById(R.id.comment);
            modify = itemView.findViewById(R.id.modify_comment);

            //아이템 클릭이벤트 처리
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //이벤트가 발생한 아이템의 위치를 알아내기 위한 메소드
                    int pos = getAdapterPosition();

                    /*
                     * notifyDataSetChanged()에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서
                     * 뷰홀더가 참조하는 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는
                     * NO_POSITION을 리턴하기 때문에 리턴값이 NO_POSTION인지 검사해줘야 한다.
                     * */
                    if(pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }

                }
            });

        }
    }
}

