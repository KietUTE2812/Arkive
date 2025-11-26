package com.example.arkivebackend.mapper;

import com.example.arkivebackend.dto.request.CreateUserRequest;
import com.example.arkivebackend.dto.request.UpdateUserRequest;
import com.example.arkivebackend.dto.response.UserResponse;
import com.example.arkivebackend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    User toUser(CreateUserRequest request);

    @Mapping(target = "address", constant = "")
    @Mapping(target = "phoneNumber", constant = "")
    @Mapping(target = "avatarUrl", constant = "")
    @Mapping(target = "bio", constant = "")
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "isVerified", source = "verified")
    @Mapping(target = "roles", source = "roles") // Sẽ tự động dùng RoleMapper
    UserResponse toUserResponse(User user);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE) // Null thì không map vào giá trị có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateUser(UpdateUserRequest request, @MappingTarget User user);
}
