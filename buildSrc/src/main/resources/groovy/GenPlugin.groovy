package groovy

import com.dzaitsev.rxviper.plugin.GeneratingTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GenPlugin implements Plugin<Project> {
  void apply(Project target) {
    target.extensions.create("rxViper", RxViperPluginExtension)
    target.task('generateScreens', type: GeneratingTask) {
      doLast {
        println("feature = ${target.rxViper.feature}")
      }
    }
  }
}