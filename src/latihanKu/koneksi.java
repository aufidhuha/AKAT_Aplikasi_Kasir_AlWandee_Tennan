/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package latihanKu;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author ASUS
 */
public class koneksi {
    
    static Connection cnVar;
    
    public static Connection getKoneksi(){
        
        try {
            String url = "jdbc:mysql://localhost:3306/kasiralwande";
            String user = "root";
            String pass = "";
            
            cnVar = DriverManager.getConnection(url, user, pass);
            return cnVar;
        } catch (SQLException sQLException) {
            JOptionPane.showMessageDialog(null, "Tidak dapat terkoneksi " + sQLException.getMessage());
            return null;
        }
        
    }
    
}
