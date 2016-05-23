/*
 * Copyright 2016 Dmytro Zaitsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rxviper.sample;

import android.app.Application;
import rxviper.sample.dagger.AppComponent;
import rxviper.sample.dagger.DaggerAppComponent;

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 *
 * @author Dmytro Zaitsev
 * @since 2016-Jun-07, 12:22
 */
public class App extends Application {
  private AppComponent mComponent;

  public AppComponent getComponent() {
    return mComponent;
  }

  @Override public void onCreate() {
    super.onCreate();
    mComponent = DaggerAppComponent.builder()
        .build();
  }
}
