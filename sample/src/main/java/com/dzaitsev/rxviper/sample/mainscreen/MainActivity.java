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

package com.dzaitsev.rxviper.sample.mainscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.dzaitsev.rxviper.sample.R;
import com.dzaitsev.rxviper.sample.StartStop;
import dagger.android.AndroidInjection;
import java.util.Set;
import javax.inject.Inject;

public final class MainActivity extends AppCompatActivity {
  @Inject Set<StartStop> startStopListeners;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_main);
  }

  @Override
  protected void onStart() {
    super.onStart();
    //noinspection Convert2streamapi
    for (StartStop listener : startStopListeners) {
      listener.onStart();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    //noinspection Convert2streamapi
    for (StartStop listener : startStopListeners) {
      listener.onStop();
    }
  }
}
