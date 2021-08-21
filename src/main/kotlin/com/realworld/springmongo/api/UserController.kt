package com.realworld.springmongo.api

import com.realworld.springmongo.api.wrappers.UserWrapper
import com.realworld.springmongo.api.wrappers.toUserWrapper
import com.realworld.springmongo.user.UserContextProvider
import com.realworld.springmongo.user.UserFacade
import com.realworld.springmongo.user.dto.UpdateUserRequest
import com.realworld.springmongo.user.dto.UserAuthenticationRequest
import com.realworld.springmongo.user.dto.UserRegistrationRequest
import com.realworld.springmongo.user.dto.UserView
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(private val userFacade: UserFacade, private val userContextProvider: UserContextProvider) {

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody @Valid request: UserWrapper<UserRegistrationRequest>): UserWrapper<UserView> {
        return userFacade.signup(request.content).toUserWrapper()
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun login(@RequestBody @Valid request: UserWrapper<UserAuthenticationRequest>): UserWrapper<UserView> {
        return userFacade.login(request.content).toUserWrapper()
    }

    @GetMapping("/user")
    suspend fun getCurrentUser(): UserWrapper<UserView> {
        val userContext = userContextProvider.getCurrentUserContext()!!
        return UserView.fromUserAndToken(userContext.user, userContext.token).toUserWrapper()
    }

    @PutMapping("/user")
    suspend fun updateUser(@RequestBody @Valid request: UserWrapper<UpdateUserRequest>): UserWrapper<UserView> {
        val userContext = userContextProvider.getCurrentUserContext()!!
        return userFacade.updateUser(request.content, userContext).toUserWrapper()
    }
}