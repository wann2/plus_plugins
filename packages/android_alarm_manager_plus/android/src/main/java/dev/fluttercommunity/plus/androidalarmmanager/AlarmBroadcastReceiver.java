// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package dev.fluttercommunity.plus.androidalarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    AlarmFlagManager.set(context, intent);

    PowerManager powerManager = (PowerManager)
      context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
      PowerManager.ACQUIRE_CAUSES_WAKEUP |
      PowerManager.ON_AFTER_RELEASE, "AlarmBroadcastReceiver:My wakelock");

    Intent startIntent = context
      .getPackageManager()
      .getLaunchIntentForPackage(context.getPackageName());

    if (startIntent != null)
      startIntent.setFlags(
        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
          Intent.FLAG_ACTIVITY_NEW_TASK |
          Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
      );

    wakeLock.acquire(3 * 60 * 1000L /*3 minutes*/);
    context.startActivity(startIntent);
    AlarmService.enqueueAlarmProcessing(context, intent);
    wakeLock.release();

    if (Build.VERSION.SDK_INT < 31)
      context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
  }
}
