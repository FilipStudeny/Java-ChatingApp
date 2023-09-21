package Interfaces;

import java.net.Inet4Address;

public interface IUser {

    void setUsername(String username);
    String getUsername();

    void setAddress(Inet4Address address);
    Inet4Address getAddress();

}
