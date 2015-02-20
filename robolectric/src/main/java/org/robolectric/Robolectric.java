package org.robolectric;

import android.app.Activity;
import android.app.Service;
import android.os.Looper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.internal.bytecode.RobolectricInternals;
import org.robolectric.res.ResourceLoader;
import android.os.Looper;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;
import org.robolectric.util.ServiceController;
import org.robolectric.internal.ShadowProvider;

//
// Imports for shims
//
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.*;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.database.CursorWrapper;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.content.ClipboardManager;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.*;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import org.robolectric.shadows.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ServiceLoader;

import static org.robolectric.Shadows.shadowOf;

public class Robolectric {
  private static final ShadowsAdapter shadowsAdapter = instantiateShadowsAdapter();

  public static void reset() {
    RuntimeEnvironment.application = null;
    RuntimeEnvironment.setRobolectricPackageManager(null);
    RuntimeEnvironment.setActivityThread(null);

    for (ShadowProvider provider : ServiceLoader.load(ShadowProvider.class)) {
      provider.reset();
    }
  }

  public static ShadowsAdapter getShadowsAdapter() {
    return shadowsAdapter;
  }

  public static <T extends Service> ServiceController<T> buildService(Class<T> serviceClass) {
    return ServiceController.of(shadowsAdapter, serviceClass);
  }

  public static <T extends Service> T setupService(Class<T> serviceClass) {
    return ServiceController.of(shadowsAdapter, serviceClass).attach().create().get();
  }

  public static <T extends Activity> ActivityController<T> buildActivity(Class<T> activityClass) {
    return ActivityController.of(shadowsAdapter, activityClass);
  }

  public static <T extends Activity> T setupActivity(Class<T> activityClass) {
    return ActivityController.of(shadowsAdapter, activityClass).setup().get();
  }

  /**
   * Execute all runnables that have been enqueued on the foreground scheduler.
   */
  public static void flushForegroundScheduler() {
    ShadowLooper.getUiThreadScheduler().advanceToLastPostedRunnable();
  }

  /**
   * Execute all runnables that have been enqueues on the background scheduler.
   */
  public static void flushBackgroundScheduler() {
    shadowOf(Looper.getMainLooper()).getScheduler().advanceToLastPostedRunnable();
  }

  private static ShadowsAdapter instantiateShadowsAdapter() {
    ShadowsAdapter result = null;
    for (ShadowsAdapter adapter : ServiceLoader.load(ShadowsAdapter.class)) {
      if (result == null) {
        result = adapter;
      } else {
        throw new RuntimeException("Multiple " + ShadowsAdapter.class.getCanonicalName() + "s found.  Robolectric has loaded multiple core shadow modules for some reason.");
      }
    }
    if (result == null) {
      throw new RuntimeException("No shadows modules found containing a " + ShadowsAdapter.class.getCanonicalName());
    } else {
      return result;
    }
  }

  ///
  /// Below here is contained shim methods as an interim to migration off of.
  ///
     /**
      * Runs any background tasks previously queued by {@link android.os.AsyncTask#execute(Object[])}.
      * <p/>
      * <p/>
      * Note: calling this method does not pause or unpause the scheduler.
      */
        public static void runBackgroundTasks() {
        getBackgroundScheduler().advanceBy(0);
      }
  
        public static Scheduler getBackgroundScheduler() {
        return ShadowApplication.getInstance().getBackgroundScheduler();
      }

        public static void setDisplayMetricsDensity(float densityMultiplier) {
        Shadows.shadowOf(getShadowApplication().getResources()).setDensity(densityMultiplier);
      }
  
        public static void setDefaultDisplay(Display display) {
        Shadows.shadowOf(getShadowApplication().getResources()).setDisplay(display);
      }

  
        /**
      * Returns a textual representation of the appearance of the object.
      *
      * @param view the view to visualize
      */
        public static String visualize(View view) {
        Canvas canvas = new Canvas();
        view.draw(canvas);
        return Shadows.shadowOf(canvas).getDescription();
      }
  
        /**
      * Returns a textual representation of the appearance of the object.
      *
      * @param canvas the canvas to visualize
      */
        public static String visualize(Canvas canvas) {
        return Shadows.shadowOf(canvas).getDescription();
      }
  
        /**
      * Returns a textual representation of the appearance of the object.
      *
      * @param bitmap the bitmap to visualize
      */
        public static String visualize(Bitmap bitmap) {
        return Shadows.shadowOf(bitmap).getDescription();
      }
  
        /**
      * Emits an xmllike representation of the view to System.out.
      *
      * @param view the view to dump
      */
        @SuppressWarnings("UnusedDeclaration")
    public static void dump(View view) {
        org.robolectric.Shadows.shadowOf(view).dump();
      }
  
        /**
      * Returns the text contained within this view.
      *
      * @param view the view to scan for text
      */
        @SuppressWarnings("UnusedDeclaration")
    public static String innerText(View view) {
        return Shadows.shadowOf(view).innerText();
      }
  
        public static ResourceLoader getResourceLoader() {
        return ShadowApplication.getInstance().getResourceLoader();
      }
  

    /**
        * Set to true if you'd like Robolectric to strictly simulate the real Android behavior when
        * calling {@link Context#startActivity(android.content.Intent)}. Real Android throws a
        * {@link android.content.ActivityNotFoundException} if given
        * an {@link Intent} that is not known to the {@link android.content.pm.PackageManager}
        *
        * By default, this behavior is off (false).
        *
        * @param checkActivities
        */
      public static void checkActivities(boolean checkActivities) {
          Shadows.shadowOf(RuntimeEnvironment.application).checkActivities(checkActivities);
        }



  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#getUiThreadScheduler()} and
   * {org.robolectric.util.Scheduler#runUiThreadTasks()} instead
   */
  @Deprecated
  public static void runUiThreadTasks() {
    getUiThreadScheduler().advanceBy(0);
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#getUiThreadScheduler()} and
   * {org.robolectric.util.Scheduler#advanceToLastPostedRunnable()} instead
   */
  @Deprecated
  public static void runUiThreadTasksIncludingDelayedTasks() {
    getUiThreadScheduler().advanceToLastPostedRunnable();
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#pauseLooper(android.os.Looper)}  instead
   */
  @Deprecated
  public static void pauseLooper(Looper looper) {
    ShadowLooper.pauseLooper(looper);
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#unPauseLooper(android.os.Looper)} instead
   */
  @Deprecated
  public static void unPauseLooper(Looper looper) {
    ShadowLooper.unPauseLooper(looper);
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#pauseMainLooper()} instead
   */
  @Deprecated
  public static void pauseMainLooper() {
    ShadowLooper.pauseMainLooper();
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#unPauseMainLooper()} instead
   */
  @Deprecated
  public static void unPauseMainLooper() {
    ShadowLooper.unPauseMainLooper();
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#idleMainLooper(long)} instead
   */
  @Deprecated
  public static void idleMainLooper(long interval) {
    ShadowLooper.idleMainLooper(interval);
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#idleMainLooperConstantly(boolean)} instead
   */
  @Deprecated
  public static void idleMainLooperConstantly(boolean shouldIdleConstantly) {
    ShadowLooper.idleMainLooperConstantly(shouldIdleConstantly);
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowLooper#getUiThreadScheduler()} instead
   */
  @Deprecated
  public static Scheduler getUiThreadScheduler() {
    return ShadowLooper.getUiThreadScheduler();
  }

  /**
   * @deprecated Use {@link org.robolectric.shadows.ShadowView#clickOn(android.view.View)} instead
   */
  @Deprecated
  public static boolean clickOn(View view) {
    return ShadowView.clickOn(view);
  }

  public static <T> T newInstanceOf(Class<T> clazz) {
    return ReflectionHelpers.callConstructor(clazz);
  }

  public static Object newInstanceOf(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      if (clazz != null) {
        return newInstanceOf(clazz);
      }
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  public static <T> T newInstance(Class<T> clazz, Class[] parameterTypes, Object[] params) {
    return ReflectionHelpers.callConstructor(clazz, ReflectionHelpers.ClassParameter.fromComponentLists(parameterTypes, params));
  }

  public static <R> R directlyOn(Object shadowedObject, String clazzName, String methodName, ReflectionHelpers.ClassParameter... paramValues) {
    try {
      Class<Object> aClass = (Class<Object>) shadowedObject.getClass().getClassLoader().loadClass(clazzName);
      return directlyOn(shadowedObject, aClass, methodName, paramValues);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static <R, T> R directlyOn(T shadowedObject, Class<T> clazz, String methodName, ReflectionHelpers.ClassParameter... paramValues) {
    return ReflectionHelpers.callInstanceMethod(shadowedObject, methodName, paramValues);
  }

  public static <R, T> R directlyOn(Class<T> clazz, String methodName, ReflectionHelpers.ClassParameter... paramValues) {
    return ReflectionHelpers.callStaticMethod(clazz, methodName, paramValues);
  }

  /**
   * @deprecated Please use {@link org.robolectric.util.ReflectionHelpers} instead
   */
  @Deprecated
  public static class Reflection {
    public static <T> T newInstanceOf(Class<T> clazz) {
      return Robolectric.newInstanceOf(clazz);
    }

    public static Object newInstanceOf(String className) {
      return Robolectric.newInstanceOf(className);
    }

    public static void setFinalStaticField(Class classWhichContainsField, String fieldName, Object newValue) {
      ReflectionHelpers.setStaticField(classWhichContainsField, fieldName, newValue);
    }

    public static Object setFinalStaticField(Field field, Object newValue) {
      Object oldValue;

      try {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        oldValue = field.get(null);
        field.set(null, newValue);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      return oldValue;
    }
  }
}
