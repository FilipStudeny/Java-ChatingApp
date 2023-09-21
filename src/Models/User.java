package Models;

import java.net.InetAddress;

public class User {

    private String userName;
    private InetAddress ipAdress;

    public User(String userName, InetAddress ipAdress){
        this.userName = userName;
        this.ipAdress = ipAdress;
    }

    public String getUserName() {
        return this.userName;
    }

    public InetAddress getIpAdress(){
        return this.ipAdress;
    }
}