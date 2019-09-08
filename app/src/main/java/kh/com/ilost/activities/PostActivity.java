package kh.com.ilost.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import kh.com.ilost.BuildConfig;
import kh.com.ilost.R;
import kh.com.ilost.models.Post;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReferencePost = FirebaseDatabase.getInstance().getReference("posts");
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    private ImageButton imgBtnAddImage;
    private EditText txtTitle,txtDate,txtTimeFrom,txtTimeTo;
    private Spinner spCat,spLocation;
    private RadioButton rbLost;
    private RadioButton rbFound;
    private Button btnSave;
    private Post product;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 101;
    private static Uri photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imgBtnAddImage = findViewById(R.id.post_img_btn_add_image);
        txtTitle = findViewById(R.id.post_txt_title);
        txtDate = findViewById(R.id.post_txt_date);
        txtTimeFrom = findViewById(R.id.post_txt_start_time);
        txtTimeTo = findViewById(R.id.post_txt_end_time);
        spCat = findViewById(R.id.post_sp_cat);
        spLocation = findViewById(R.id.post_sp_location);
        rbLost = findViewById(R.id.post_rbtn_lost);
        rbFound = findViewById(R.id.post_rbtn_found);
        btnSave = findViewById(R.id.post_btn_save);
        product = new Post();

        rbLost.setChecked(true);
        imgBtnAddImage.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if( id == R.id.post_img_btn_add_image){
            if(getPermissionGrated()) openCameraIntent();
        }
        else if(id == R.id.post_btn_save){
            if(!txtTitle.getText().toString().equals("")
                    && !txtTimeFrom.toString().equals("")
                    && !txtTimeTo.toString().equals("")
                    && !txtDate.toString().equals("")
                    && !getSpinnerItemValue(spCat).equals("Category")
                    && !getSpinnerItemValue(spLocation).equals("Location")){
                String key = databaseReferencePost.push().getKey();
                if(rbFound.isChecked()){
                    product.setPtype("Found");
                }else product.setPtype("Lost");
                product.setPtitle(txtTitle.getText().toString());
                product.setPdate(txtDate.getText().toString());
                product.setPcat(getSpinnerItemValue(spCat));
                product.setPtimefrom(txtTimeFrom.getText().toString());
                product.setPtimeto(txtTimeTo.getText().toString());
                product.setPlocation(getSpinnerItemValue(spLocation));
                uploadImage(key);
                databaseReferencePost.child(key).child("pid").setValue(key);
                databaseReferencePost.child(key).child("ptitle").setValue(product.getPtitle());
                databaseReferencePost.child(key).child("ptype").setValue(product.getPtype());
                databaseReferencePost.child(key).child("puid").setValue(firebaseUser.getUid());
                databaseReferencePost.child(key).child("pdate").setValue(product.getPdate());
                databaseReferencePost.child(key).child("ptimefrom").setValue(product.getPtimefrom());
                databaseReferencePost.child(key).child("ptimeto").setValue(product.getPtimeto());
                databaseReferencePost.child(key).child("pcat").setValue(product.getPcat());
                databaseReferencePost.child(key).child("plocation").setValue(product.getPlocation());
                databaseReferencePost.child(key).child("purl").setValue(product.getPurl());

            }
            else Toast.makeText(PostActivity.this,"Please Fill All Information",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (photoPath != null) {
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoPath);
                    imgBtnAddImage.setImageBitmap(imageBitmap);
                    imgBtnAddImage.setScaleType(ImageView.ScaleType.CENTER);
                    imgBtnAddImage.setAdjustViewBounds(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.d("dfs", "onActivityResult: "+data);
            }
        }
        else photoPath = null;
    }
    private String getSpinnerItemValue(Spinner spinner){
        return spinner.getSelectedItem().toString();
    }
    private void openCameraIntent() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoPath);
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }
    private void uploadImage(final String key) {
        if(photoPath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Posting...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("post_images/"+ UUID.randomUUID().toString());
            ref.putFile(photoPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            product.setPurl(taskSnapshot.getDownloadUrl()+"");
                            databaseReferencePost.child(key).child("purl").setValue(product.getPurl());
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    private boolean getPermissionGrated(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCameraIntent();
            }
        }
    }
}
