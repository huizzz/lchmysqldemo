package com.lch.autoConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dly_lee
 */

@ConfigurationProperties(prefix = "lch.mybatis")
public class LchProperties {

    private String packageScanner = "";

    private String driver = "com.mysql.jdbc.Driver";

    private String url;

    private String username ;

    private String password ;


    public String getPackageScanner() {
        return packageScanner;
    }

    public void setPackageScanner(String packageScanner) {
        this.packageScanner = packageScanner;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LchProperties{" +
                "packageScanner='" + packageScanner + '\'' +
                ", driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
