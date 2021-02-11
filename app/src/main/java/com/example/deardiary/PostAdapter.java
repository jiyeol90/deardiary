package com.example.deardiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    ArrayList<PostItem> items;

    public PostAdapter(Context context, ArrayList<PostItem> items) {
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

        View itemView = inflater.inflate(R.layout.recycler_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;

        PostItem item = items.get(position);
        vh.tvName.setText(item.getUserId());
        vh.tvDate.setText(item.getDate());

        if(item.getUserProfile().equals("default")) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_profile_default).override(90, 90).into(vh.userProfile);
        } else {
            Glide.with(holder.itemView.getContext()).load(item.getUserProfile()).override(90, 90).into(vh.userProfile);
        }

        vh.userProfile.setTag(holder.getAdapterPosition());
        vh.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                PostItem item = items.get(position);
                String userId = item.getUserId();
                Toast.makeText(context, Integer.toString(position) + "번째 아이템, 아이디 : " + userId , Toast.LENGTH_SHORT).show();

                //로그인한 유저가 자신의 프로필 사진을 눌렀을때 MyAccountFragment로 이동한다.
                if(UserInfo.getInstance().getId().equals(userId)) {
                    String fragmentTag = MyHomeFragment.class.getClass().getSimpleName();
                    Log.i("fragmentTag", fragmentTag);
                    //R.id.bottomNavi
                    Intent intent = new Intent(context, AppStartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);// 플래그로 백스택 관리 필요
                } else {
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fm.findFragmentById(R.id.main_frame);
                    ft.replace(R.id.main_frame, new MyAccountFragment());
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
//                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                //fragmentTransaction.replace(R.id.main_frame, homeFragment);
//                Fragment fragment = fm.findFragmentById(R.id.main_frame);
//                //ft.remove(fragment);
//                ft.replace(R.id.main_frame, new MyAccountFragment());
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.addToBackStack(fragmentTag);
//                //ft.add(R.id.main_frame, new MyAccountFragment());
//                ft.commit();
            }
        });

        //Tag값을 이용해서 위치를 알아낸다.
        //UserID와 같지 않다면 삭제버튼을 감춘다.
        if(!UserInfo.getInstance().getId().equals(item.getUserId())) {
            vh.btnEdit.setVisibility(View.INVISIBLE);
        } else {
            vh.btnEdit.setTag(holder.getAdapterPosition());
            vh.btnEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    PostItem item = items.get(position);
                    String postIndex = item.getNo();
                    String userId = item.getUserId();

                    //Toast.makeText(context, Integer.toString(position) + "번째 아이템, 이름 : " + name + " , no : " + no, Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(context)

                            .setTitle("페이지를 삭제하시겠습니까?")

                            //Todo 사진촬영 기능 구현할 것
                            //.setPositiveButton("사진촬영", cameraListener)

                            .setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePost(postIndex, userId);
                                    items.remove(position);
                                    notifyDataSetChanged();
                                }
                            })

                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
        
        //vh.tvMsg.setText(item.getMsg());
        Glide.with(holder.itemView.getContext()).load(item.getImgPath()).into(vh.iv);
    }

    //포스팅 삭제
    private void deletePost(String postIndex, String userId) {

        Toast.makeText(context, postIndex + ": no , userID : " + userId,  Toast.LENGTH_SHORT).show();

        String uRl = "http://3.36.92.185/modifydata/delete_post.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                BusProvider.getInstance().post(new BusEvent(true));
                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", userId);
                param.put("postIndex", postIndex);
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequestQueue(request);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvDate;
        CircleImageView userProfile;
        ImageButton btnEdit;
        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_userId);
            tvDate = itemView.findViewById(R.id.tv_date);
            userProfile=itemView.findViewById(R.id.userProfile);
            btnEdit = itemView.findViewById(R.id.edit_button);
            iv = itemView.findViewById(R.id.iv);

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
