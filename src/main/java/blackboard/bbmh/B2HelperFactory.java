/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blackboard.bbmh;

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 *
 * @author arossi
 */
public class B2HelperFactory {
    
    public static List<B2Helper> getB2s() {
        Logging.writeLog("Start: getB2s");
        
        // TODO: refactoring
        // replacr SQL query with public java.util.List<blackboard.platform.plugin.PlugIn> getPlugIns()
        // http://library.blackboard.com/ref/16ce28ed-bbca-4c63-8a85-8427e135a710/blackboard/platform/plugin/PlugInManager.html#getPlugIns--
        
        Connection dbConnection = Db.getConnection();
        List<B2Helper> b2s = new ArrayList<B2Helper>();
        SimpleDateFormat anotherdbformatter = new SimpleDateFormat("yyyy-MM-dd");
        String qrystr = "";
        switch (DbServerInfo.getDatabaseType()) {
            case "oracle":
                qrystr = "SELECT name, vendor_id, handle, vendor_name, available_flag, dtmodified FROM plugins ORDER BY name";
                break;
            case "mssql":
                qrystr = "SELECT name, vendor_id, handle, vendor_name, available_flag, dtmodified FROM plugins ORDER BY name";
                break;
            case "pgsql":
                qrystr = "SELECT name, vendor_id, handle, vendor_name, available_flag, dtmodified FROM plugins ORDER BY name";
                break;
            default:
            // nothing to do
        }
        Statement dbStatement = Db.createStatement(dbConnection);
        ResultSet rs = null;
        try {
            try {
                boolean wasExecuted = dbStatement.execute(qrystr);
                if (wasExecuted) {
                    rs = dbStatement.getResultSet();
                    while (rs.next()) {
                        B2Helper b2local = new B2Helper(rs.getString("vendor_id"), rs.getString("handle"));
                        b2local.setName(rs.getString("name"));
                        b2local.setLocalizedName(rs.getString("name"));
                        b2local.setVendorName(rs.getString("vendor_name"));
                        b2local.setVersion();
                        b2local.setAvailableFlag(rs.getString("available_flag"));
                        b2local.setDateModified(anotherdbformatter.parse(rs.getString("dtmodified")));
                        b2local.setHasWarFile();

                        b2s.add(b2local);
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (dbStatement != null) {
                    dbStatement.close();
                }
                if(dbConnection != null){
                    dbConnection.close();
                }
            }
        } catch (Exception e) {
            // TODO: log in logs
            //dbVersion = "exception " + e + " " ;
        }
        Logging.writeLog("End: getB2s");
        return b2s;
    }
}