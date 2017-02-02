package groovy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GeneratingTask extends DefaultTask {
  @TaskAction
  def generate() {
    println 'Generating...'
  }
}
