package groovy

import com.dzaitsev.rxviper.plugin.FeatureOptions
import org.gradle.api.Action

/**
 * Created by dmitriyzaitsev on 2/1/17.
 */
class RxViperPluginExtension {
  private final FeatureOptions feature

  RxViperPluginExtension() {
    feature = new FeatureOptions()
  }

  void features(Action<FeatureOptions> action) {
    action.execute(feature)
  }

  FeatureOptions getFeature() {
    return feature
  }
}
