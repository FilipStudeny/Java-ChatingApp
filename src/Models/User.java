package Models;

import Interfaces.IUser;

import java.net.Inet4Address;
import java.net.Socket;

public class User implements IUser {
    private String username = null;
    private Inet4Address address = null;

    public User(Inet4Address address){
        this.address = address;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setAddress(Inet4Address address) {
        this.address = address;
    }

    @Override
    public Inet4Address getAddress() {
        return this.address;
    }
}
