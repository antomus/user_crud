package com.crud.demo.utility;

import com.crud.demo.dto.UserDTO;
import com.crud.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTOBuilder {
    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setBio(user.getBio());
        return userDTO;
    }
}
