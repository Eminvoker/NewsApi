package com.invok.newsapi;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    private static  final int REQUEST_CODE_PICK_IMAGE = 1;
    private static  final int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private final String IMAGE_TYPE = "image/*";

    private CircleImageView header_iv;
    private TextView pathTv;

    private String newPicUrl;

    private String imagePath;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        user = (User) getIntent().getSerializableExtra("user");
        header_iv = (CircleImageView) findViewById(R.id.header_iv);
        pathTv = (TextView) findViewById(R.id.path_tv);
        if(user.getHeaderUrl() != null){
            Glide.with(InfoActivity.this).load(user.getHeaderUrl()).asBitmap().into(header_iv);
        }

        initAppTitle();
    }

    public void getImageFromAlbum(View v){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType(IMAGE_TYPE);//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                //判断手机版本系统号
                if(Build.VERSION.SDK_INT >= 19){
                    //4.4及以上系统使用这个方法处理图片
                    handleImageOnKitkat(data);
                }else {
                    //4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                Uri uri = data.getData();
                Glide.with(InfoActivity.this).load(uri).asBitmap().into(header_iv);
            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
                Uri uri = data.getData();
                //to do find the path of pic
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitkat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(InfoActivity.this,uri)){
            //如果是document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果不是document类型的Uri，则使用普通的方式处理
            imagePath = getImagePath(uri,null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            if(cursor.moveToFirst()){
                //按我个人理解 这个是获得用户选择的图片的索引值 cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //header_iv.setImageBitmap(bitmap);
           // Glide.with(InfoActivity.this).load(imagePath).into(header_iv);
            pathTv.append(imagePath);
        }else{
            Toast.makeText(InfoActivity.this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public void save(View v){
        if(imagePath != null) {
            singleUpload(imagePath);
        }else {
            Toast.makeText(getApplicationContext(),"请先选择图片，再保存！",Toast.LENGTH_SHORT).show();
        }
    }

    private void singleUpload(String path){
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    pathTv.append("上传文件成功:" + bmobFile.getFileUrl());
                    newPicUrl = bmobFile.getUrl();
                    updateUser();
                }else{
                    pathTv.append("上传文件失败：" + e.getMessage());
                }

            }
        });
    }

    private void updateUser() {
        BmobQuery<User> query = new BmobQuery();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    final String oldUrl = user.getHeaderUrl();
                    user.setHeaderUrl(newPicUrl);
                    user.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Log.d(TAG, "done: "+"上传头像并保存成功。"+newPicUrl);
                                Toast.makeText(InfoActivity.this,"头像修改成功！",Toast.LENGTH_SHORT).show();
                                deleteHeaderFile(oldUrl);

                            }else{
                                Log.d(TAG, "done: "+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteHeaderFile(String url) {
        BmobFile file = new BmobFile();
        file.setUrl(url);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                }else{
                }
            }
        });
    }

    public void initAppTitle(){
        CircleImageView topMe;
        ImageView topBack;
        topBack = (ImageView) findViewById(R.id.top_back);
        topMe = (CircleImageView) findViewById(R.id.top_me);
        topMe.setVisibility(View.INVISIBLE);
        topMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),MeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if(newPicUrl != null){
                    user.setHeaderUrl(newPicUrl);
                }
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

}
