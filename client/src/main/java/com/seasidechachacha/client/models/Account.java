package com.seasidechachacha.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    String userId;
    String password;
    int roleId;

    public Account(@NonNull String userId, int roleId){
        this.userId = userId;
        this.roleId = roleId;
    }
}
