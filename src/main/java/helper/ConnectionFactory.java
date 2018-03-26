/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author junior
 */
public class ConnectionFactory {

    public static Connection createConnection() throws SQLException {
//        String url = "jdbc:mysql://10.32.138.10:3306/educafacil_biritiba";
//        String user = "admindb";
//        String password = "@Admin2k";
        String url = "jdbc:mysql://localhost:3306/educafacil_biritiba";
        String user = "junior";
        String password = "arroto00##";

        Connection conexao = null;
        if (conexao == null) {
            conexao = (Connection) DriverManager.getConnection(url, user, password);
        }

        return conexao;
    }
}
