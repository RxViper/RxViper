package groovy

public class FeatureOptions {
  String featureName
  String packageName

  public String fullPackage() {
    return packageName + '.' + featureName;
  }

  @Override
  public String toString() {
    return "com.dzaitsev.rxviper.gradle.FeatureOptions{featureName='$featureName', packageName='$packageName'}";
  }
}
