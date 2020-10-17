package com.aanchal.godric.quizzzed;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SharedMemory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUpProfileActivity extends AppCompatActivity {

    ImageView pic,edit;
    EditText name;
    Button done;
    String username,imagename="default.png";
    private InputStream is;
    private Bitmap bm = null;

    SharedPreferences preferences;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);
        init();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SetUpProfileActivity.this);
                dialog.setContentView(R.layout.profile_pic_dialog);

                Button camera =  dialog.findViewById(R.id.take_photo_camera);
                Button gallery = dialog.findViewById(R.id.choose_from_gallery);
                Button cancel = dialog.findViewById(R.id.cancel_dialog);
                //open camera
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(SetUpProfileActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(SetUpProfileActivity.this,
                                    new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = name.getText().toString();
                if(is!=null)
                    imagename = username+".jpg";
                else
                    imagename = "default.png";
                //insert in database
                Call<User> call = apiInterface.registerUser(username,imagename);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        String result = user.getStatus();
                        if(result.equals("Success")){
                            preferences.edit().putString("username",username).commit();
                            Intent intent = new Intent(SetUpProfileActivity.this,MainMenuActivity.class);
                            startActivity(intent);
                        }
                        else{
                            //name.setError("Already exists");
                            Toast.makeText(SetUpProfileActivity.this,"Username already exists",Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(SetUpProfileActivity.this,""+t,Toast.LENGTH_LONG).show();
                        //Toast.makeText(SetUpProfileActivity.this,"Your Profile Setup Failed, Try Again",Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    if(is!=null)
                    uploading();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        MultipartBody.Part body = MultipartBody.Part.createFormData(username,username+".jpg", requestFile);

        Call<ImageBean> call = apiInterface.postImg(body);
        call.enqueue(new Callback<ImageBean>() {
            @Override
            public void onResponse(Call<ImageBean> call, Response<ImageBean> response) {
                //Toast.makeText(SetUpProfileActivity.this,"Upload Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ImageBean> call, Throwable t) {
                //Toast.makeText(SetUpProfileActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, "result Code "+data.getData(), Toast.LENGTH_SHORT).show();
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null ){
            //Toast.makeText(this, "get image "+data, Toast.LENGTH_SHORT).show();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            pic.setImageBitmap(bitmap);

            Uri filePath = getImageUri(SetUpProfileActivity.this,bitmap);
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
                Picasso.get().load(data.getData()).into(pic);
                is = getContentResolver().openInputStream(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void init() {
        pic = findViewById(R.id.setup_picture);
        edit = findViewById(R.id.setup_edit_icon);
        name = findViewById(R.id.setup_username);
        done = findViewById(R.id.setup_done);

        preferences = getSharedPreferences("name", Context.MODE_PRIVATE);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
    }

    public void onBackPressed(){
        final Dialog dialog = new Dialog(SetUpProfileActivity.this);
        dialog.setContentView(R.layout.exit_dialog);

        Button yes = dialog.findViewById(R.id.exit_dialog_yes);
        Button no = dialog.findViewById(R.id.exit_dialog_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
