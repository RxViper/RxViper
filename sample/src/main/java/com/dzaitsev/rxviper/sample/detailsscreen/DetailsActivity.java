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

package com.dzaitsev.rxviper.sample.detailsscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.dzaitsev.rxviper.sample.R;

public final class DetailsActivity extends AppCompatActivity {
  public static final String NAME    = "name";
  public static final String CHECKED = "checked";
  public static final String TYPE    = "type";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    final TextView text = (TextView) findViewById(R.id.details);
    final Intent intent = getIntent();
    text.setText(intent.getStringExtra(NAME));
    text.append("\n:\n" + intent.getStringExtra(TYPE));
    text.append(intent.getBooleanExtra(CHECKED, false) ? "\n:\nCHECKED" : "\n:\nNOT CHECKED");
  }
}
