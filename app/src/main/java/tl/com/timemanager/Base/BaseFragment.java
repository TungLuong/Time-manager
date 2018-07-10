package tl.com.timemanager.Base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    public void onBackPressed() {
       showDialogDelete();
    }
    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thoát");
        builder.setMessage("Bạn có chắc chắn muốn thoát không");
        builder.setCancelable(false);
        builder.setPositiveButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((BaseActivity)getActivity()).onBackRoot();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}
