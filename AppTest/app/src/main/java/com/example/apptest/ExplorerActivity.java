package com.example.apptest;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExplorerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_layout);



    }

    class TextAdapter extends BaseAdapter{

        private List<String> data = new ArrayList<>();

        private boolean[] selection;

        public void setData(List<String> data) {
            if (data != null) {
                this.data.clear();
                if(data.size() > 0) {
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }

        void setSelection(boolean[] selection) {
            if(selection != null) {
                this.selection = new boolean[selection.length];
                for(int i = 0; i < selection.length; i++) {
                    this.selection[i] = selection[i];
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.textItem)));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String itemPath = getItem(position);
            if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("ppt") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("pptx")) {
                holder.info.setTextColor(Color.RED);
            }
            else if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("word") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("doc") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("docx") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("hwp")) {
                holder.info.setTextColor(Color.rgb(24,104,195));
            }
            else if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("png") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("jpg") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("jpeg") ||
                    itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("bmp")) {
                holder.info.setTextColor(Color.rgb(98,69,149));
            }
            else if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("pdf")) {
                holder.info.setTextColor(Color.rgb(255,165,0));
            }
            else if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("apk")) {
                holder.info.setTextColor(Color.GREEN);
            }
            else if (itemPath.substring(itemPath.lastIndexOf('.')+1).equalsIgnoreCase("gif")) {
                holder.info.setTextColor(Color.MAGENTA);
            }
            else {
                holder.info.setTextColor(Color.BLACK);
            }
            holder.info.setText(itemPath.substring(itemPath.lastIndexOf('/')+1));
            if(selection != null) {
                if (selection[position]) {
                    holder.info.setBackgroundColor(Color.argb(20,8,8,8));
                }
                else {
                    holder.info.setBackgroundColor(Color.WHITE);
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView info;
            ViewHolder(TextView info) {
                this.info = info;
            }
        }

    }

    private static final int REQUEST_PERMISSIONS = 1234;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSIONS_COUNT = 2;
    @SuppressLint("NewApi")
    private boolean arePermissionsDenied() {
        int p = 0;
        while (p < PERMISSIONS_COUNT) {
            if(checkSelfPermission(PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            p++;
        }
        return false;
    }

    private boolean isFileManagerInitialized = false;
    private boolean[] selection;
    private File[] files;
    private List<String> filesList;
    private int filesFoundCount;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        if (!isFileManagerInitialized) {

            String rootPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

            final File dir = new File(rootPath);
            files = dir.listFiles();
            final TextView pathOutput = findViewById(R.id.pathOutput);
            pathOutput.setText("Now at : " + rootPath.substring(rootPath.lastIndexOf('/')+1));

            filesFoundCount = files.length;
            final ListView listView = findViewById(R.id.listView);
            final TextAdapter textAdapter1 = new TextAdapter();
            listView.setAdapter(textAdapter1);

            filesList = new ArrayList<>();
            for (int i = 0; i < filesFoundCount; i++) {
                filesList.add(String.valueOf(files[i].getAbsolutePath()));
            }
            textAdapter1.setData(filesList);

            selection = new boolean[files.length];

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                    selection[index] = !selection[index];
                    textAdapter1.setSelection(selection);

                    boolean isAtLeastOneSelected = false;
                    for(int i = 0; i < selection.length; i++) {
                        if (selection[i]) {
                            isAtLeastOneSelected = true;
                            break;
                        }
                    }
                    if (isAtLeastOneSelected) {
                        findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    }
                    else {
                        findViewById(R.id.bottomBar).setVisibility(View.GONE);
                    }
                    return false;
                }
            });

            final Button button_1 = findViewById(R.id.button_1);
            final Button deleteButton = findViewById(R.id.delete);
            final Button button_3 = findViewById(R.id.button_3);
            final Button button_4 = findViewById(R.id.button_4);
            final Button button_5 = findViewById(R.id.button_5);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(ExplorerActivity.this);
                    deleteDialog.setTitle("Delete Confirmation");
                    deleteDialog.setMessage("Are you sure to delete this file?");
                    deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {
                            for(int i = 0; i < files.length;i++) {
                                if (selection[i]) {
                                    deleteFileOrFolder(files[i]);
                                    for (int j = 0; j < files.length;j++) {
                                        selection[j] = false;
                                    }
                                }
                            }
                            files = dir.listFiles();
                            filesFoundCount = files.length;
                            filesList.clear();
                            for (int i = 0; i < filesFoundCount; i++) {
                                filesList.add(String.valueOf(files[i].getAbsolutePath()));
                            }
                            textAdapter1.setData(filesList);
                        }
                    });
                    deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {
                            dialogInterface.cancel();
                        }
                    });
                    deleteDialog.show();
                }
            });

            isFileManagerInitialized = true;
        }
    }

    private void deleteFileOrFolder(File fileOrFolder) {
        if (fileOrFolder.isDirectory()) {
            if (fileOrFolder.list().length == 0) {
                fileOrFolder.delete();
            }
            else {
                String files[] = fileOrFolder.list();
                for(String temp:files) {
                    File fileToDelete = new File(fileOrFolder, temp);
                    deleteFileOrFolder(fileToDelete);
                }
                if (fileOrFolder.list().length == 0) {
                    fileOrFolder.delete();
                }
            }
        }
        else {
            fileOrFolder.delete();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if(arePermissionsDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
            else{
                onResume();
            }
        }
    }
}