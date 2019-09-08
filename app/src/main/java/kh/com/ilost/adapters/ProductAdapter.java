package kh.com.ilost.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kh.com.ilost.R;
import kh.com.ilost.models.Post;
import kh.com.ilost.view.holders.ProductViewHolder;

/**
 * Created by Daly on 3/24/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private Context context;
    private ArrayList<Post> products;
    public ProductAdapter(Context context, ArrayList<Post> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.view_holder_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Post product = products.get(position);
        holder.set(context,product);
    }

    @Override
    public int getItemCount() {
        if (products.size() != 0)
            return products.size();
        return 0;
    }
}
