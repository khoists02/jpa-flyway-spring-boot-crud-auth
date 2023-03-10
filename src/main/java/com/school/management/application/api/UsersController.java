package com.school.management.application.api;

import com.school.management.GenericProto;
import com.school.management.PlaystationsProto;
import com.school.management.UserProto;
import com.school.management.application.criteria.PlaystationsSearchCriteria;
import com.school.management.application.criteria.UsersSearchCriteria;
import com.school.management.application.model.Playstation;
import com.school.management.application.model.User;
import com.school.management.application.services.UsersService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Transactional
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserProto.UserResponses getAll(
            @RequestParam(name = "keyword") Optional<String> keyword,
            @PageableDefault(sort = "username") Pageable pageable
    ) {
        UsersSearchCriteria usersSearchCriteria = new UsersSearchCriteria();
        usersSearchCriteria.setKeyword(keyword.orElse(null));
        Page<User> users =  usersService.finAll(pageable, usersSearchCriteria);
        return UserProto.UserResponses.newBuilder()
                .addAllContent(
                        users.stream().map(user -> UserProto.UserResponse.newBuilder()
                                        .setUsername(user.getUsername())
                                        .setEmail(user.getEmail())
                                        .build())
                                .toList())
                .setPageable(mapper.map(users, GenericProto.PageableResponse.Builder.class))
                .build();
    }
}
