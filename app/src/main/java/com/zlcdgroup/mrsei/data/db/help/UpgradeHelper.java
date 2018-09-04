/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.zlcdgroup.mrsei.data.db.help;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.zlcdgroup.mrsei.data.db.dao.DaoMaster;


public class UpgradeHelper  extends DaoMaster.OpenHelper {

  public  static  String TAG="UpgradeHelper";

  public UpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
    super(context, name, factory);
  }

  public UpgradeHelper(Context context, String name) {
    super(context, name);
  }

  /**
   * Here is where the calls to upgrade are executed
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    System.out.println("onUpgrade--->oldVersion="+oldVersion+":newVersion="+newVersion);
        /* i represent the version where the user is now and the class named with this number implies that is upgrading from i to i++ schema */
    for (int i = oldVersion; i < newVersion; i++) {
      try {
        System.out.println("version="+i);
                /* New instance of the class that migrates from i version to i++ version named DBMigratorHelper{version that the db has on this moment} */
        AbstractMigratorHelper migratorHelper = (AbstractMigratorHelper) Class.forName("com.zlcdgroup.mrsei.data.db.help.DBMigrationHelper" + i).newInstance();
        System.out.println("migratorHelper="+(null == migratorHelper));
        if (migratorHelper != null) {

                    /* Upgrade de db */
          migratorHelper.onUpgrade(db);
        }

      } catch (ClassNotFoundException | ClassCastException | IllegalAccessException | InstantiationException e) {

        Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
        break;
      }


    }
  }
}
