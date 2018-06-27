package tl.com.timemanager.Base;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    public void onBackPressed() {
        ((BaseActivity)getActivity()).onBackRoot();
    }
}
