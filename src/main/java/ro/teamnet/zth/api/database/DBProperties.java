package ro.teamnet.zth.api.database;

/**
 * Created by Raluca.Russindilar on 02.07.2016.
 */
public interface DBProperties {
    //TODO de inlocuit cu IP-ul din Docker
    String IP = "192.168.99.100";
    String PORT = "49161";
    //TODO de inlocuit 1cu utilizatorul vostru
    String USER = "aldinca";
    //TODO de inlocuit cu parola voastra
    String PASS = "aldinca";
    String SID = "xe";
    String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

}
