package kh.com.ilost.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kh.com.ilost.R;
import kh.com.ilost.models.Comment;

public class CommentViewHolder extends RecyclerView.ViewHolder implements ValueEventListener {
    private TextView txtUserCommentName;
    private TextView txtComment;
    private ImageView ivUserCommentProfile;
    private DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users");
    private String username;
    public CommentViewHolder(View itemView) {
        super(itemView);
        txtUserCommentName = itemView.findViewById(R.id.vh_post_detail_user_name);
        txtComment = itemView.findViewById(R.id.vh_post_detail_user_comment);
        ivUserCommentProfile = itemView.findViewById(R.id.vh_post_detail_user_img);
    }
    public void set(Comment comment){
        DatabaseReference database = databaseReferenceUser.child(comment.getUserId());
        txtComment.setText(comment.getComment());
        database.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        username = (String)dataSnapshot.child("name").getValue();
        txtUserCommentName.setText(username);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
