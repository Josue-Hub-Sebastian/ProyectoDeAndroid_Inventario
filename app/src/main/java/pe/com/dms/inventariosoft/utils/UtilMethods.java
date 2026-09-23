package pe.com.dms.inventariosoft.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.ArrayRes;

import com.google.android.material.tabs.TabLayout;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alvaro Santa Cruz on 23/02/2017.
 */

public class UtilMethods {
    static String TAG = UtilMethods.class.getSimpleName();

    private static Toast toast;
    private static Context context;

    public static void initialize(Context context) {
        UtilMethods.context = context;
        toast = Toast.makeText(context, "message_blank", Toast.LENGTH_SHORT);
    }

    public static void showToast(String message) {
        toast.setText(message);
        toast.show();
    }

    public static String nvl(String string, String value) {
        return string == null ? value : string;
    }

    public static String cleanString(String string) {
        return nvl(string, "").trim();
    }

    public static boolean isEmpty(String string) {
        return TextUtils.isEmpty(cleanString(string));
    }

    public static String getTimestap() {
        return calendarToString("yyyyMMddHHmmss");
    }

    public static String getTimestapEPC() {
        return calendarToString("dd/MM/yyyy HH:mm:ss");
    }

    public static float parseFloat(String stringFloat) {
        stringFloat = isEmpty(stringFloat) ? "0" : stringFloat;
        return Float.parseFloat(stringFloat);
    }

    public static double parseDouble(String stringDouble) {
        stringDouble = isEmpty(stringDouble) ? "0" : stringDouble;
        return Double.parseDouble(stringDouble);
    }

    public static <T> T clone(T object, Class<T> clazz) {
        return new Gson().fromJson(new Gson().toJson(object), clazz);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void notYet() {
        showToast("Proximamente...");
    }

    public static int dpFromPx(Context context, float px) {
        return Math.round((px / context.getResources().getDisplayMetrics().density));
    }

    public static int pxFromDp(Context context, float dp) {
        return Math.round((dp * context.getResources().getDisplayMetrics().density));
    }

    public static int pxFromSp(Context context, float dp) {
        return Math.round((dp * context.getResources().getDisplayMetrics().scaledDensity));
    }

    public static void shouldNeverHappen() {
        throw new RuntimeException("ASCT. This should never occurs");
    }

    public static Calendar stringToCalendar(String dateString, String format) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        try {
            if (!TextUtils.isEmpty(dateString)) {
                calendar.setTime(sdf.parse(dateString));
            }
        } catch (ParseException e) {
            Log.e("ParseException", e.getMessage());
        }
        return calendar;
    }

    public static int getVisibility(boolean flg) {
        return flg ? View.VISIBLE : View.GONE;
    }

    public static String calendarToString() {
        return calendarToString(Calendar.getInstance());
    }

    public static String calendarToString(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.BD_DATETIME_FORMAT, Locale.US);
        return sdf.format(date);
    }

    public static String calendarStringToString(String dateString, String formatInput, String formatOutput) {
        Calendar calendar = stringToCalendar(dateString, formatInput);
        return calendarToString(calendar, formatOutput);
    }

    public static String calendarToString(Calendar calendar, String format) {
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(date);
    }

    public static String calendarToString(String format) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(date);
    }

    public static String getCammelCase(String field) {
        String[] names = field.split("_");
        String fullName = names[0];
        for (int i = 1; i < names.length; i++) {
            String name = names[i];
            fullName += (name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        return fullName;
    }

    public static void hideKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void toggleEnable(View view, boolean flag) {

        if (view instanceof TabLayout) {
//            view.setEnabled(flag);
        } else if (view instanceof Spinner) {
            view.setEnabled(flag);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = ((ViewGroup) view);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                toggleEnable(viewGroup.getChildAt(i), flag);
            }
        } else {
            view.setEnabled(flag);
            view.setClickable(flag);
        }
    }

    public static boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    //    TODO: To use this method, you need to add: <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI() {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.
                TELEPHONY_SERVICE);
        return telephonyManager != null ? telephonyManager.getDeviceId() : null;
    }

    public static int parseInt(String stringExtra) {
        if (TextUtils.isEmpty(stringExtra)) stringExtra = "0";
        return Integer.parseInt(stringExtra);
    }

    public static void setupDummyAdapter(Spinner spinner, @ArrayRes int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(spinner.getContext(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static void setWeight(TextView view, int weight) {
        if (weight == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, weight));
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String hexToString(String hex) {
        Log.e(TAG, "hexToString hex: " + hex);
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        Log.e(TAG, "hexToString sb.toString(): " + sb.toString());
        return sb.toString();
    }

    public static boolean checkCameraHardware(PackageManager packageManager) {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            Log.d(TAG, "checkCameraHardware si tiene camara: ");
            return true;
        } else {
            // no camera on this device
            Log.d(TAG, "checkCameraHardware no tiene camara: ");
            return false;
        }
    }
}
