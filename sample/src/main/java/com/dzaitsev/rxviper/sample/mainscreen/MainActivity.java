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
import com.dzaitsev.rxviper.sample.App;
import com.dzaitsev.rxviper.sample.R;
import com.dzaitsev.rxviper.sample.mainscreen.di.MainScreenModule;
import com.dzaitsev.rxviper.sample.mainscreen.di.MainScreenSubcomponent;
import com.dzaitsev.rxviper.sample.mainscreen.view.MainView;

public final class MainActivity extends AppCompatActivity {
  public  MainView    mainView;
  private ScopeHolder scope;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_main);
    mainView = (MainView) findViewById(R.id.view_main);

    scope = (ScopeHolder) getLastCustomNonConfigurationInstance();

    if (scope == null) {
      final MainScreenModule module = new MainScreenModule();
      scope = new ScopeHolder(module, ((App) getApplication()).getComponent().plus(module));
    }

    scope.module.setMainActivity(this);
    scope.subcomponent.inject(mainView);
  }

  @Override
  public ScopeHolder onRetainCustomNonConfigurationInstance() {
    return scope;
  }

  @Override
  protected void onStart() {
    super.onStart();
    mainView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mainView.onStop(isChangingConfigurations());
    if (isFinishing()) {
      scope.module.setMainActivity(null);
    }
  }

  final static class ScopeHolder {
    final MainScreenModule       module;
    final MainScreenSubcomponent subcomponent;

    ScopeHolder(MainScreenModule module, MainScreenSubcomponent subcomponent) {
      this.module = module;
      this.subcomponent = subcomponent;
    }
  }
}
