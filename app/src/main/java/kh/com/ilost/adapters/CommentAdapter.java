package kh.com.ilost.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kh.com.ilost.R;
import kh.com.ilost.models.Comment;
import kh.com.ilost.view.holders.CommentViewHolder;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    private Context context;
    private ArrayList<Comment> comments;
    public CommentAdapter (Context context,ArrayList<Comment> comments){
        this.context = context;
        this.comments = comments;
    }
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.view_holder_post_detail_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.set(comment);
    }
    @Override
    public int getItemCount() {
        if (comments.size() != 0)
            return comments.size();
        return 0;
    }
}
