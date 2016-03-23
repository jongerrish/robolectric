package org.robolectric.shadows;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ReflectionHelpers;

import java.io.FileDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Shadow for {@link android.os.ServiceManager}.
 */
@Implements(value = ServiceManager.class, isInAndroidSdk = false)
public class ShadowServiceManager {

  @Implementation
  public static IBinder getService(String name) {
    return new IBinder() {
      @Override
      public String getInterfaceDescriptor() throws RemoteException {
        return null;
      }

      @Override
      public boolean pingBinder() {
        return false;
      }

      @Override
      public boolean isBinderAlive() {
        return false;
      }

      @Override
      public IInterface queryLocalInterface(String descriptor) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
          Class<?> binderClass = classLoader.loadClass(descriptor);
          return (IInterface) Proxy.newProxyInstance(classLoader, new Class[]{binderClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
              return ReflectionHelpers.nullish(method);
            }
          });
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void dump(FileDescriptor fd, String[] args) throws RemoteException {

      }

      @Override
      public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {

      }

      @Override
      public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return false;
      }

      @Override
      public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {

      }

      @Override
      public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return false;
      }
    };
  }

  @Implementation
  public static void addService(String name, IBinder service) {
  }

  @Implementation
  public static IBinder checkService(String name) {
    return null;
  }

  @Implementation
  public static String[] listServices() throws RemoteException {
    return null;
  }

  @Implementation
  public static void initServiceCache(Map<String, IBinder> cache) {
  }
}
