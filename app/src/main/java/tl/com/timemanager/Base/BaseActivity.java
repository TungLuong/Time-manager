package tl.com.timemanager.Base;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()){
                    ((BaseFragment)fragment).onBackPressed();
                    return;
                }
            }
        }
        onBackRoot();
    }

    public final void onBackRoot(){
        super.onBackPressed();
    }
}
