/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alyqamar.Coursco.dal.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author home
 */
public class DBConnection {

    private Connection con = null;
    private String Path;
    private final String propFileName = "database.properties";
    private final String propFolderPath = "/com/alyqamar/Coursco/config/";

    public DBConnection() {
        //you can change config path from here
        setPath(propFolderPath + propFileName);
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String config) {
        this.Path = config;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public void Connect() {
        String JbdcCon, databaseName, localhost, password, user, securityBool, encryptBool;

        Properties prop = new Properties();

        try {
            prop.load(getClass().getResourceAsStream(getPath()));
        } catch (IOException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        JbdcCon = prop.getProperty("JbdcCon");
        localhost = prop.getProperty("localhost");
        String url = JbdcCon + localhost;

        databaseName = prop.getProperty("databaseName");
        user = prop.getProperty("user");
        password = prop.getProperty("password");
        securityBool = prop.getProperty("securityBool");
        encryptBool = prop.getProperty("encryptBool");

        String connection = (url + ';' + "databaseName=" + databaseName + ';'
                + "user=" + user + ';' + "password=" + password + ';'
                + "integratedSecurity=" + securityBool + ';' + " encrypt =" + encryptBool + ';');

        try {
            setCon(DriverManager.getConnection(connection));

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
