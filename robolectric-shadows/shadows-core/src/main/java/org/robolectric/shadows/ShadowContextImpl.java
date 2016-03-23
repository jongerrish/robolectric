package org.robolectric.shadows;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.fakes.RoboSharedPreferences;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.robolectric.internal.Shadow.directlyOn;
import static org.robolectric.util.ReflectionHelpers.ClassParameter.from;

/**
 * Shadow for {@code android.content.ContextImpl}.
 */
@Implements(className = ShadowContextImpl.CLASS_NAME)
public class ShadowContextImpl {
  public static final String CLASS_NAME = "android.app.ContextImpl";
  private final Map<String, RoboSharedPreferences> roboSharedPreferencesMap = new HashMap<>();
  private Map<String, Map<String, Object>> sharedPreferenceMap = new HashMap<>();
  private ContentResolver contentResolver;

  @RealObject
  private Context realObject;

  @Implementation
  public File validateFilePath(String name, boolean createDirectory) {
    File dir;
    File f = new File(name);

    if (f.isAbsolute()) {
      dir = f.getParentFile();
    } else {
      dir = directlyOn(realObject, "android.app.ContextImpl", "getDatabasesDir");
      f = directlyOn(realObject, "android.app.ContextImpl", "makeFilename", from(File.class, dir), from(String.class, name));
    }

    if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
      FileUtils.setPermissions(dir.getPath(),
          FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IXOTH,
          -1, -1);
    }

    return f;
  }

  @Implementation
  public void startIntentSender(IntentSender intent, Intent fillInIntent,
                    int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
    intent.sendIntent(realObject, 0, fillInIntent, null, null, null);
  }

  @Implementation
  public ComponentName startService(Intent service) {
    return ShadowApplication.getInstance().startService(service);
  }

  @Implementation
  public void startActivity(Intent intent) {
    ShadowApplication.getInstance().startActivity(intent);
  }

  @Implementation
  public void sendBroadcast(Intent intent) {
    ShadowApplication.getInstance().sendBroadcast(intent);
  }

  @Implementation
  public ClassLoader getClassLoader() {
    return this.getClass().getClassLoader();
  }

  @Implementation
  public boolean bindService(Intent intent, final ServiceConnection serviceConnection, int i) {
    return ShadowApplication.getInstance().bindService(intent, serviceConnection, i);
  }

  @Implementation
  public void unbindService(final ServiceConnection serviceConnection) {
    ShadowApplication.getInstance().unbindService(serviceConnection);
  }

  @Implementation
  public int checkCallingPermission(String permission) {
    return checkPermission(permission, -1, -1);
  }

  @Implementation
  public int checkCallingOrSelfPermission(String permission) {
    return checkPermission(permission, -1, -1);
  }

  @Implementation
  public ContentResolver getContentResolver() {
    if (contentResolver == null) {
      contentResolver = new ContentResolver(realObject) {
        @Override
        protected IContentProvider acquireProvider(Context c, String name) {
          return null;
        }

        @Override
        public boolean releaseProvider(IContentProvider icp) {
          return false;
        }

        @Override
        protected IContentProvider acquireUnstableProvider(Context c, String name) {
          return null;
        }

        @Override
        public boolean releaseUnstableProvider(IContentProvider icp) {
          return false;
        }

        @Override
        public void unstableProviderDied(IContentProvider icp) {

        }
      };
    }
    return contentResolver;
  }

  @Implementation
  public void sendBroadcast(Intent intent, String receiverPermission) {
    ShadowApplication.getInstance().sendBroadcast(intent, receiverPermission);
  }

  @Implementation
  public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
    ShadowApplication.getInstance().sendOrderedBroadcast(intent, receiverPermission);
  }

  @Implementation
  public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver,
                                   Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
    ShadowApplication.getInstance().sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode,
        initialData, initialExtras);
  }

  @Implementation
  public void sendStickyBroadcast(Intent intent) {
    ShadowApplication.getInstance().sendStickyBroadcast(intent);
  }

  @Implementation
  public int checkPermission(String permission, int pid, int uid) {
    return ShadowApplication.getInstance().checkPermission(permission, pid, uid);
  }

  @Implementation
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    return ShadowApplication.getInstance().registerReceiverWithContext(receiver, filter, null, null, realObject);
  }

  @Implementation
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
    return ShadowApplication.getInstance().registerReceiverWithContext(receiver, filter, broadcastPermission, scheduler, realObject);
  }

  @Implementation
  public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
    ShadowApplication.getInstance().unregisterReceiver(broadcastReceiver);
  }

  @Implementation
  public PackageManager getPackageManager() {
    return RuntimeEnvironment.getPackageManager();
  }

  @Implementation
  public boolean stopService(Intent name) {
    return ShadowApplication.getInstance().stopService(name);
  }

  @Implementation
  public void startActivity(Intent intent, Bundle options) {
    ShadowApplication.getInstance().startActivity(intent, options);
  }

  @Implementation
  public void startActivities(Intent[] intents) {
    for (int i = intents.length - 1; i >= 0; i--) {
      startActivity(intents[i]);
    }
  }

  @Implementation
  public void startActivities(Intent[] intents, Bundle options) {
    for (int i = intents.length - 1; i >= 0; i--) {
      startActivity(intents[i], options);
    }
  }

  @Implementation
  public SharedPreferences getSharedPreferences(String name, int mode) {
    if (!roboSharedPreferencesMap.containsKey(name)) {
      roboSharedPreferencesMap.put(name, new RoboSharedPreferences(sharedPreferenceMap, name, mode));
    }

    return roboSharedPreferencesMap.get(name);
  }

  @Implementation
  public int getUserId() {
    return 0;
  }

  @Implementation
  public File getExternalCacheDir() {
    return Environment.getExternalStorageDirectory();
  }

  @Implementation
  public File getExternalFilesDir(String type) {
    return Environment.getExternalStoragePublicDirectory(type);
  }
}
