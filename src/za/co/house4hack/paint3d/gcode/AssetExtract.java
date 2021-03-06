// This string is autogenerated by ChangeAppSettings.sh, do not change
// spaces amount
package za.co.house4hack.paint3d.gcode;

import java.io.*;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.content.res.AssetManager;

import org.xeustechnologies.jtar.*;

import za.co.house4hack.paint3d.Main;

class AssetExtract {

    private AssetManager mAssetManager = null;
    private Context mActivity = null;
    
    AssetExtract(Context act) {
        mActivity = act;
        mAssetManager = act.getAssets();
    }
    
    public boolean extractTar(String asset, String target) {

        byte buf[] = new byte[1024 * 1024];
        
        InputStream assetStream = null;
        TarInputStream tis = null;
        
        try {
            assetStream = mAssetManager.open(asset, AssetManager.ACCESS_STREAMING);
            tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(assetStream, 8192)), 8192));
        } catch (IOException e) {
            Log.e("python", "opening up extract tar", e);
            return false;
        }
        
        while (true) {
            TarEntry entry = null;

            try {
                entry = tis.getNextEntry();
            } catch ( java.io.IOException e ) {
                Log.e("python", "extracting tar", e);
                return false;
            }

            if ( entry == null ) {
                break;
            }

            Log.i("python", "extracting " + entry.getName());
            
            if (entry.isDirectory()) {

                try {
                    new File(target +"/" + entry.getName()).mkdirs();
                } catch ( SecurityException e ) { };

                continue;
            }

            OutputStream out = null;
            String path = target + "/" + entry.getName();

            try {
                out = new BufferedOutputStream(new FileOutputStream(path), 8192);
            } catch ( FileNotFoundException e ) {
            	Log.e(Main.LOG_TAG,"File not found",e);
            } catch ( SecurityException e ) { 
            	Log.e(Main.LOG_TAG,"Security Exception",e);
            };

            if ( out == null ) {
                Log.e("python", "could not open " + path);
                return false;
            }

            try {
                while (true) {
                    int len = tis.read(buf);
                    
                    if (len == -1) {
                        break;
                    }
                    
                    out.write(buf, 0, len);
                }
                
                out.flush();
                out.close();
            } catch ( java.io.IOException e ) {
                Log.e("python", "extracting zip", e);
                return false;
            }
        }

        try {
            tis.close();
            assetStream.close();
        } catch (IOException e) {
            // pass
        }
            
        return true;
    }
}
