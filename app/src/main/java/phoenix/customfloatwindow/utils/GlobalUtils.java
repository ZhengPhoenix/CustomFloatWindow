package phoenix.customfloatwindow.utils;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by zhenghui on 2017/2/16.
 */
public class GlobalUtils {

    private static final String TAG = "GlobalUtils";

    public static boolean isLargeMIUIv8(){
        int version = 0;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getStringMethod = sysClass.getDeclaredMethod("get", String.class);
            String versionString = (String)getStringMethod.invoke(sysClass, "ro.miui.ui.version.name");
            if (!TextUtils.isEmpty(versionString)) {
                if (versionString.startsWith("V")) {
                    version = Integer.valueOf(versionString.substring(1, versionString.length()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (version >= 8) {
            Log.d(TAG, "isLargeMIUIv8: true");
            return true;
        }
        return false;
    }
}
