/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blackboard.bbmh;

import blackboard.platform.config.BbConfig;
import blackboard.platform.config.ConfigurationServiceFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author arossi
 */


public class AppServerInfo {
    public String osName = "";
    
    public static String getOsName() {
        return System.getProperty("os.name");
    }
    
    public static String getOsArch() {
        return System.getProperty("os.arch");
    }
    
    public static String getOsVersion() {
        return System.getProperty("os.version");
    }
    
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }    
    
    public static String getServerTime(String format) {
        SimpleDateFormat appFormatter = new SimpleDateFormat(format);
        java.util.Date dtAppServerTime = new java.util.Date();
        TimeZone tz = TimeZone.getDefault();
        return appFormatter.format(dtAppServerTime) + " - "  + tz.getID() + " - " + tz.getDisplayName();
    }   

    public static String getUrl() {
        String fullHostName = BbConfig.APPSERVER_FULLHOSTNAME;
        return ConfigurationServiceFactory.getInstance().getBbProperty( fullHostName, fullHostName + " not found in bb-config.properties");
    }  

    public static String getBaseDirPath() {
        // get either BASEDIR or BASEDIR_WIN
        // TODO: This does not do what you think it does - it uses the literal string constant BASEDIR_WIN as the
        // default value if the requested key is not found.  it doesn't do a cascading lookup
        return ConfigurationServiceFactory.getInstance().getBbProperty(BbConfig.BASEDIR, BbConfig.BASEDIR_WIN);
    }

    public static String getContentDirPath() {
        // get either BASE_SHARED_DIR  or BASE_SHARED_DIR_WIN
        // TODO: This does not do what you think it does - it uses the literal string constant BASE_SHARED_DIR_WIN as the
        // default value if the requested key is not found.  it doesn't do a cascading lookup
        return ConfigurationServiceFactory.getInstance().getBbProperty(BbConfig.BASE_SHARED_DIR, BbConfig.BASE_SHARED_DIR_WIN );
    }

    public static String getExternalStorageDirPath() {
        // get either BASE_SHARED_DIR  or BASE_SHARED_DIR_WIN
        return ConfigurationServiceFactory.getInstance().getBbProperty(BbConfig.CS_EXTERNAL_STORAGE_LOCATION );
    }


    
    
    
    public static double getDiskUsage(String fs) {
        Logging.writeLog("Start: getDiskUsage");
        double du = -1;
        Path path = FileSystems.getDefault().getPath(fs);
        du = size(path);
        du = du/(1024d*1024d*1024d);
        BigDecimal bdc = new BigDecimal(du);
        bdc = bdc.setScale(2,BigDecimal.ROUND_HALF_UP);
        du = bdc.doubleValue();
        /*
        // all in GB
        File ff = new File (fs);
        try {
            du = (ff.getTotalSpace() - ff.getFreeSpace()) / (1024*1024*1024);
        } catch (Exception e) {
            //Logger.getLogger(AppServerInfo.class.getName()).log(Level.SEVERE, null, e);
            //throw new RuntimeException("Problem while trying to get Building Block Config Directory", e);
        }
        */
        Logging.writeLog("End: getDiskUsage");
        return du;
    }

    // copied from https://stackoverflow.com/questions/2149785/get-size-of-folder-or-file
    public static long size(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }
    
}
