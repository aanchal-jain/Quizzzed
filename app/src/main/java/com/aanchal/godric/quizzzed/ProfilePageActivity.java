package com.aanchal.godric.quizzzed;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePageActivity extends AppCompatActivity {

    private CircleImageView profile_pic;
    private EditText usrname;
    private TextView coins;
    private String image_path,username,new_username,imagename;
    private ImageView edit;
    private Button saveChanges,stats;
    private InputStream is;
    private Bitmap bm = null;

    SharedPreferences preferences;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        init();

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePageActivity.this,StatsActivity.class);
                startActivity(intent);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ProfilePageActivity.this);
                dialog.setContentView(R.layout.profile_pic_dialog);

                Button camera =  dialog.findViewById(R.id.take_photo_camera);
                Button gallery = dialog.findViewById(R.id.choose_from_gallery);
                Button cancel = dialog.findViewById(R.id.cancel_dialog);
                //open camera
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ProfilePageActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(ProfilePageActivity.this, new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);

                            dialog.dismiss();
                        }
                    }
                });
                //open gallery
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Image"),1234);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(is!=null)
                        uploading();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new_username = usrname.getText().toString();

                if(is!=null)
                    imagename = new_username+".jpg";
                else
                    imagename = "default.png";

                Call<User> call = apiInterface.updateUser(username,new_username,imagename);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        String res = user.getStatus();
                        if(res.equals("Success")){
                            preferences.edit().putString("username",new_username).commit();
                            Toast.makeText(ProfilePageActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                        }
                        else if(res.equals("Failure") && (!new_username.equals(username))){
                            Toast.makeText(ProfilePageActivity.this,"Username already exists",Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                            //Toast.makeText(ProfilePageActivity.this,"Update Failure",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploading() throws IOException{
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];
        int len = 0;

        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }
        byte[] b;
        b = byteBuff.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), b);
        MultipartBody.Part body = MultipartBody.Part.createFormData(username, username+".jpg", requestFile);

        Call<ImageBean> call = apiInterface.postImg(body);
        call.enqueue(new Callback<ImageBean>() {
            @Override
            public void onResponse(Call<ImageBean> call, Response<ImageBean> response) {
                Toast.makeText(ProfilePageActivity.this,"Upload Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ImageBean> call, Throwable t) {
                Toast.makeText(ProfilePageActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Toast.makeText(this, "result Code "+data.getData(), Toast.LENGTH_SHORT).show();
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null ){
            Toast.makeText(this, "get image "+data, Toast.LENGTH_SHORT).show();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profile_pic.setImageBitmap(bitmap);

            Uri filePath = getImageUri(ProfilePageActivity.this,bitmap);
            try{
                //bm = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                //profile_pic.setImageBitmap(bm);
                is = getContentResolver().openInputStream(filePath);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if(requestCode == 1234 && resultCode ==Activity.RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try{
                Picasso.get().load(data.getData()).into(profile_pic);
                is = getContentResolver().openInputStream(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        Intent intent = getIntent();
        image_path = intent.getStringExtra("img_path");

        saveChanges = findViewById(R.id.save_profile_changes);
        stats = findViewById(R.id.button_stats);
        profile_pic = findViewById(R.id.profile_picture);
        usrname = findViewById(R.id.profile_username);
        coins = findViewById(R.id.profile_coins);
        edit = findViewById(R.id.edit_icon);

        Picasso.get().load(image_path).into(profile_pic);

        preferences = getSharedPreferences("name", Context.MODE_PRIVATE);
        username = preferences.getString("username","");

        usrname.setText(username);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<UserDetailsList> call = apiInterface.getInfo(username);
        call.enqueue(new Callback<UserDetailsList>() {
            @Override
            public void onResponse(Call<UserDetailsList> call, Response<UserDetailsList> response) {
                UserDetailsList userDetailsList = response.body();
                ArrayList<UserDetails> userDetailsArrayList = userDetailsList.getUsers();
                int int_coins = userDetailsArrayList.get(0).getCoins();
                coins.setText(""+int_coins);
            }

            @Override
            public void onFailure(Call<UserDetailsList> call, Throwable t) {
            }
        });
    }

    /*public void onBackPressed(){
        startActivity(new Intent(ProfilePageActivity.this,MainMenuActivity.class));
    }*/
}

