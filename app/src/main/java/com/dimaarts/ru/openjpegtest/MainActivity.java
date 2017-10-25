package com.dimaarts.ru.openjpegtest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dimaarts.ru.openjpegtest.utils.FileUtils;

import org.openJpeg.OpenJPEGJavaEncoder;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private ConvertAsyncTask mTask;
    private String mInputPath;
    private String mOutputPath;
    private static final String INPUT_FILE_NAME = "arch.png";
    private static final String OUTPUT_FILE_NAME = "arch.jp2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AssetManager assetsManager = getAssets();
        String dataDirectory = null;
        try {
            dataDirectory = FileUtils.getDataDirectory(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mInputPath = dataDirectory + File.separator + INPUT_FILE_NAME;

        FileUtils.copyAsset(assetsManager, INPUT_FILE_NAME, mInputPath);

        mOutputPath = dataDirectory + File.separator;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File inputFile = new File(mInputPath);
                File outputFile = new File(mOutputPath);
                if(!inputFile.exists() || !outputFile.exists()) {
                    Toast toast = Toast.makeText(MainActivity.this, "Input file or output directory is not exists", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                runConvertTask();
            }
        });
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(
                this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Compressing...");
        mProgressDialog.show();
    }

    private static class ConvertAsyncTask extends AsyncTask<Void,Void,Long> {
        private WeakReference<ProgressDialog> progressDialog;
        private WeakReference<AppCompatActivity> activity;
        private String mInputPath;
        private String mOutputPath;

        ConvertAsyncTask(ProgressDialog progressDialog, AppCompatActivity activity, String inputPath, String outputPath) {
            this.progressDialog = new WeakReference<ProgressDialog>(progressDialog);
            this.activity = new WeakReference<AppCompatActivity>(activity);
            mInputPath = inputPath;
            mOutputPath = outputPath;
        }

        private long convert() {
            OpenJPEGJavaEncoder encoder = new OpenJPEGJavaEncoder();
            String[] params = new String[8];
            params[0] = "-i";
            params[1] = mInputPath;
            params[2] = "-o";
            params[3] = mOutputPath;
            params[4] = "-t";
            params[5] = "1024,1024";
            params[6] = "-r";
            params[7] = "100";

            return encoder.encodeImageToJ2K(params);
        }

        @Override
        protected void onPostExecute(Long o) {
            super.onPostExecute(o);
            ProgressDialog progressDialog = this.progressDialog.get();
            AppCompatActivity activity = this.activity.get();
            if(progressDialog!=null)
                progressDialog.dismiss();

            if(activity!=null) {
                int inputFileSize = FileUtils.getFileSize(mInputPath);
                int outputFileSize = FileUtils.getFileSize(mOutputPath);

                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Result");
                alertDialog.setMessage(o==0? "Processed! Input file size = " + inputFileSize+" kb, output file size = " + outputFileSize + " kb": "Something is wrong!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            return convert();
        }
    }

    private void runConvertTask() {
        showProgress();
        mTask = new ConvertAsyncTask(mProgressDialog,this, mInputPath, mOutputPath + File.separator + OUTPUT_FILE_NAME);
        mTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeTask();
    }

    private void removeTask() {
        if(mTask!=null)
            mTask.cancel(true);
        mTask=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
