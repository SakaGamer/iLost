package kh.com.ilost.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import kh.com.ilost.R;
import kh.com.ilost.adapters.CommentAdapter;
import kh.com.ilost.adapters.ProductAdapter;
import kh.com.ilost.models.Comment;
import kh.com.ilost.models.Post;

public class PostDetailActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferencePost = FirebaseDatabase.getInstance().getReference("posts");
    DatabaseReference databaseReferenceComment ;
    private EditText edtComment;
    private ImageView ivSendComment,ivPost;
    private TextView txtUserPost,txtPostTitle;
    private RecyclerView rvComment;
    private ArrayList<Comment> comments;
    private String postId;
    private String userId;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        edtComment = findViewById(R.id.edt_post_detail_comment);
        ivSendComment = findViewById(R.id.iv_post_detail_send_comment);
        ivPost = findViewById(R.id.iv_post_detail_image);
        txtUserPost = findViewById(R.id.post_detail_user_name);
        txtPostTitle = findViewById(R.id.tv_post_detail_post_title);
        rvComment = findViewById(R.id.rcv_post_detail_comment);
        comments = new ArrayList<>();
        getData();
        layoutManager = new LinearLayoutManager(this);
        if(!postId.equals("")){
            getPostByID();
        }
        ivSendComment.setOnClickListener(this);
        initComment();
    }
    private void getData(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            txtUserPost.setText(bundle.getString("username"));
            userId = bundle.getString("userId");
            postId = bundle.getString("postId");
        }
    }
    private void getPostByID(){
        databaseReferencePost.child(postId).addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Post post = dataSnapshot.getValue(Post.class);
        if(post!=null){
            txtPostTitle.setText(post.getPtitle());
            Glide.with(getApplicationContext()).load(post.getPurl()).into(ivPost);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
    private void initComment(){
        databaseReferenceComment = databaseReferencePost.child(postId).child("comment");
        databaseReferenceComment.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                comments.add(comment);
                Log.d("app", "onChildAdded: "+ comments.size());
                if(comments != null)
                {
                    Log.d("d", "onDataChange: "+comments.get(0).getUserId());
                    initCommentList();
                }
                Log.d("d", "onChildAdded: "+dataSnapshot.toString());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id= view.getId();
        if(id == R.id.iv_post_detail_send_comment){
            if(firebaseUser!=null){
                Comment comment = new Comment();
                comment.setCommentId(databaseReferencePost.push().getKey());
                comment.setComment(edtComment.getText().toString());
                comment.setUserId(firebaseUser.getUid());
                comment.setPostId(postId);

                databaseReferenceComment.child(comment.getCommentId()).setValue(comment);
            }
        }
    }
    private void initCommentList(){
        Collections.reverse(comments);
        CommentAdapter commentAdapter= new CommentAdapter(getApplicationContext(),comments);
        rvComment.setLayoutManager(layoutManager);
        rvComment.setAdapter(commentAdapter);
    }
}
