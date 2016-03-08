package com.bitdubai.android_core.app.common.version_1.apps_manager;

import android.content.Context;

import com.bitdubai.android_core.app.ApplicationSession;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.FermatAppType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by Matias Furszyfer on 2016.03.05..
 */
public class AppsConfiguration {

    private final FermatAppsManager fermatAppsManager;

    private final String APPS_CONFIGURATION_FILE = "installed_app_configuration.txt";

    public AppsConfiguration(FermatAppsManager fermatAppsManager) {
        this.fermatAppsManager = fermatAppsManager;
    }

    public HashMap<String, FermatAppType> readAppsCoreInstalled(){
        try{
            Context context = ApplicationSession.getInstance().getApplicationContext();
            FileInputStream fIn = context.openFileInput(APPS_CONFIGURATION_FILE);
            ObjectInputStream isr = new ObjectInputStream(fIn);
            return (HashMap<String, FermatAppType>) isr.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }
        return new HashMap<>();
    }

    public void updateAppsCoreInstalled(){
        HashMap<String,FermatAppType> appsInstalledInDevice = new HashMap<>();
        // Aplicaciones instaladas en el dispositivo separadas por tipo
        for (FermatAppType fermatAppType : FermatAppType.values()) {
            for (String key : fermatAppsManager.selectRuntimeManager(fermatAppType).getListOfAppsPublicKey()) {
                appsInstalledInDevice.put(key,fermatAppType);
            }
        }
        Context context = ApplicationSession.getInstance().getApplicationContext();
        try {
            FileOutputStream fOut = context.openFileOutput(APPS_CONFIGURATION_FILE,
                    context.MODE_PRIVATE);
            ObjectOutputStream osw = new ObjectOutputStream(fOut);
            osw.writeObject(appsInstalledInDevice);
            osw.flush();
            osw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
