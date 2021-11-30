package com.seasidechachacha.client.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Account {
    private String name;
    private int birthYear;
    private String address;
}
