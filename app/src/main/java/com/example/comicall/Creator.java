package com.example.comicall;

import java.io.Serializable;

public class Creator  implements Serializable {

    private String name;
    private String role;

    public Creator(String name, String role){
        this.name = name;
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    public String getName() {
        return this.name;
    }
}
