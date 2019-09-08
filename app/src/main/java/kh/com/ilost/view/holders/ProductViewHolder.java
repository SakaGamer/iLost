package kh.com.ilost.view.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kh.com.ilost.R;
import kh.com.ilost.activities.PostDetailActivity;
import kh.com.ilost.models.Comment;
import kh.com.ilost.models.Post;

/**
 * Created by Daly on 3/24/2018.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder implements ValueEventListener, View.OnClickListener {

    private DatabaseReference databaseReferencePost;
    private TextView txtUsername;
    private TextView txtProductName;
    private ImageView ivUserProfile;
    private ImageView ivProductImg;
    private EditText edtComment;
    private Button btnComment;
    private DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users");
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Comment comment;
    private ImageView ivSendComment;
    private Post post;
    private CardView cvDetail;
    private Context context;
    private String username;

    public ProductViewHolder(View itemView) {
        super(itemView);
        txtUsername = itemView.findViewById(R.id.vh_product_user_name);
        txtProductName = itemView.findViewById(R.id.vh_product_name);
        ivUserProfile = itemView.findViewById(R.id.vh_product_user_img);
        ivProductImg = itemView.findViewById(R.id.vh_product_image);
        edtComment = itemView.findViewById(R.id.edt_product_comment);
        btnComment = itemView.findViewById(R.id.btn_product_comment);
        ivSendComment = itemView.findViewById(R.id.iv_product_send_comment);
        cvDetail = itemView.findViewById(R.id.cv_product_detail);
        ivSendComment.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        cvDetail.setOnClickListener(this);

    }

    public void set(Context context, Post post) {
        this.context = context;
        this.post = post;
        databaseReferencePost = FirebaseDatabase.getInstance().getReference("posts").child(post.getPid()).child("comment");
        DatabaseReference database = databaseReferenceUser.child(post.getPuid());
        database.addValueEventListener(this);
        txtProductName.setText(post.getPtitle());
        Glide.with(context).load(post.getPurl()).into(ivProductImg);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        username = (String) dataSnapshot.child("name").getValue();
        txtUsername.setText(username);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("Home", "loadPost:onCancelled", databaseError.toException());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_product_comment) {
            edtComment.requestFocus();
        } else if (id == R.id.iv_product_send_comment) {
            if (firebaseUser != null) {
                comment = new Comment();
                comment.setCommentId(databaseReferencePost.push().getKey());
                comment.setComment(edtComment.getText().toString());
                comment.setUserId(firebaseUser.getUid());
                comment.setPostId(post.getPid());
                databaseReferencePost.child(comment.getCommentId()).setValue(comment);
            }

        } else if (id == R.id.cv_product_detail) {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("userID", post.getPuid());
            intent.putExtra("username", username);
            intent.putExtra("postId", post.getPid());
            context.startActivity(intent);
        }
    }
}
