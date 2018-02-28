package com.shengz.dribbbo.view.bucket_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.shengz.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shengzhong on 2017/11/24.
 */

public class BucketDialogFragment  extends DialogFragment{
    public static final String TAG = "NewBucketDialogFragment";
    public static final String KEY_BUCKET_NAME = "bucket_name";
    public static final String KEY_BUCKET_DESCRIPTION = "bucket_description";

    @BindView(R.id.new_bucket_name) EditText newBucketName;
    @BindView(R.id.new_bucket_description) EditText newBucketDescription;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_bucket,null);
        ButterKnife.bind(this,view);
        builder.setView(view)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(newBucketName.getText().toString() != null){
                            Intent intent = new Intent();
                            intent.putExtra(KEY_BUCKET_NAME, newBucketName.getText().toString());
                            if(newBucketDescription != null){
                                intent.putExtra(KEY_BUCKET_DESCRIPTION,newBucketDescription.getText().toString());
                            }
                            getTargetFragment().onActivityResult(BucketListFragment.REQ_CODE_NEW_BUCKET,
                                    Activity.RESULT_OK, intent);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BucketDialogFragment.this.getDialog().cancel();

                    }
                });

        return builder.create();
    }
}
