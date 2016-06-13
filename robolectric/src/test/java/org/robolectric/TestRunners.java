package org.robolectric;

import org.junit.runners.model.InitializationError;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FsFile;

import java.util.Locale;

import static org.robolectric.util.TestUtil.resourceFile;

public class TestRunners {

  public static class WithDefaults extends RobolectricTestRunner {
    public static final String SDK_TARGETED_BY_MANIFEST = "-v23";
    
    public WithDefaults(Class<?> testClass) throws InitializationError {
      super(testClass);
      Locale.setDefault(Locale.ENGLISH);
    }

    @Override
    protected AndroidManifest createAppManifest(FsFile manifestFile, FsFile resDir, FsFile assetDir, String packageName) {
      return new AndroidManifest(resourceFile("TestAndroidManifest.xml"), resourceFile("res"), resourceFile("assets"), packageName);
    }
  }

  public static class MultiApiWithDefaults extends MultiApiRobolectricTestRunner {

    public MultiApiWithDefaults(Class<?> testClass) throws Throwable {
      super(testClass);
      Locale.setDefault(Locale.ENGLISH);
    }

    protected TestRunnerForApiVersion createTestRunner(Integer integer) throws InitializationError {
      return new DefaultRunnerWithApiVersion(getTestClass().getJavaClass(), integer);
    }

    private static class DefaultRunnerWithApiVersion extends TestRunnerForApiVersion {

      DefaultRunnerWithApiVersion(Class<?> type, Integer apiVersion) throws InitializationError {
        super(type, apiVersion);
      }

      @Override
      protected AndroidManifest createAppManifest(FsFile manifestFile, FsFile resDir, FsFile assetDir, String packageName) {
        return new AndroidManifest(resourceFile("TestAndroidManifest.xml"), resourceFile("res"), resourceFile("assets"), packageName);
      }
    }
  }
}
