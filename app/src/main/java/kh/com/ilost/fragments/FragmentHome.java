package kh.com.ilost.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kh.com.ilost.R;
import kh.com.ilost.activities.LoginActivity;
import kh.com.ilost.activities.PostActivity;
import kh.com.ilost.adapters.ProductAdapter;
import kh.com.ilost.models.Post;

import static com.facebook.AccessTokenManager.TAG;


public class FragmentHome extends Fragment implements View.OnClickListener, ValueEventListener {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
    private FirebaseAuth fAuth;
    private TextView txUemail;
    private RecyclerView rclProduct;
    private ArrayList<Post> products;
    private LinearLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private Button btnPost;


    public FragmentHome() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        Button btnLogout = root.findViewById(R.id.home_btn_logout);
        btnPost = root.findViewById(R.id.home_btn_post);
        txUemail = root.findViewById(R.id.home_txt_user_email);
        rclProduct = root.findViewById(R.id.home_rcl_product);
        layoutManager = new LinearLayoutManager(getContext());
       // layoutManager.setReverseLayout(true);
        products = new ArrayList<>();
        databaseReference.addValueEventListener(this);
        root.setFocusable(true);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                updateUI(null);
            }
        });
        fAuth = FirebaseAuth.getInstance();
        btnPost.setOnClickListener(this);

        return root;
    }
    private void logout() {
        fAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show();
        Intent googleIntent = new Intent(getContext(), LoginActivity.class);
        startActivity(googleIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null){
            updateUI(currentUser);
        }

    }

    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null) {
            txUemail.setText(firebaseUser.getDisplayName());
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.home_btn_post) {
            startActivity(new Intent(getActivity(), PostActivity.class));
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        products.clear();
        for(DataSnapshot dataSnapshotData : dataSnapshot.getChildren())
        {
            Post post = dataSnapshotData.getValue(Post.class);
            products.add(post);
        }
        if(products!=null)
        {
            Collections.reverse(products);
            productAdapter = new ProductAdapter(getContext(),products);
            rclProduct.setLayoutManager(layoutManager);
            rclProduct.setAdapter(productAdapter);

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("Home", "loadPost:onCancelled", databaseError.toException());
    }
}
