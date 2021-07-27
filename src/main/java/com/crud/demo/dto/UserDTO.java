package com.crud.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {

    private Long id;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private String username;
    private String bio;

}
