package com.gamevision.service;

import com.gamevision.model.binding.UserRegisterBindingModel;
import com.gamevision.model.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    boolean isUserNameFree(String username); //for register

    boolean isEmailFree(String email); //for register

    void registerAndLogin(UserRegisterBindingModel userRegisterBindingModel);

    //  UserServiceModel findByUsernameAndPassword(String username, String password);
    // void loginUser(Long id, String username);

    Optional<UserEntity> findUserById(Long id);

    //  void updateUser(UserEntity user);

    void initUsers();
}
