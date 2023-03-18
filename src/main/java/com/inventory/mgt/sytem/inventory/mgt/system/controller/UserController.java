package com.inventory.mgt.sytem.inventory.mgt.system.controller;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.SetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.Deactivate;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.SignupRequest;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.LoginHistoryRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.response.ApiResponse;
import com.inventory.mgt.sytem.inventory.mgt.system.model.LoginHistory;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Role;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    LoginHistoryRepo loginHistoryRepo;

    // GET ALL USERS
   @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam("size") Integer pageNumber,
                                                  @RequestParam("number") Integer pageSize) {
        Page<User> users = userService.getAllUsers(pageNumber, pageSize);
        return ResponseEntity.ok().body(users);

    }
    @GetMapping("user/{id}")
    public User getUser(@PathVariable("id") Long id){
        return  userService.getUser(id);
    }
    // DEACTIVATE AN ADMIN
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/deactivate/{user_id}")
    public ResponseEntity<ApiResponse> deactivateUser(@RequestBody Deactivate deactivated,
                                                      @PathVariable("user_id")Long id , Principal principal) {
        Assert.notNull(deactivated, "Reason for deactivation must  be provided");

        userService.deactivateUser(deactivated, id, principal);
        return  new ResponseEntity<>(new ApiResponse(true, "Admin has been deactivated!"), HttpStatus.CREATED);

    }

    // ACTIVATE AN ADMIN;
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/activate/{id}")
  public String activateUser(@PathVariable("id") Long id){
      return userService.activateUser(id);
  }

    // get currently logged in user;
    @GetMapping("/principal")
   // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String loggedInUser( Principal principal) {
     String loggedInUser =  principal.getName();
     return  loggedInUser;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public  String createUser(@RequestBody SignupRequest request, Principal principal){

        return userService.createUser(request, principal);
    }
    @PutMapping(value = "/reset")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public  String createPassword(@RequestBody SetPassword setPassword){
        return userService.resetPassword(setPassword);
    }

   @GetMapping("history")
   @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity <List<LoginHistory>> adminLoginHistory(){
       return new ResponseEntity<>(userService.adminLoginHistory(), HttpStatus.OK);
    }
    // endpoint to show how many times admin logged in
    @GetMapping("/count")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Long getAdminLoggedInCount(){
      return  loginHistoryRepo.count();
    }
    public  User getLoggedInUser(Principal principal){
        return  userService.getLoggedInUser(principal);
    }





    @Autowired
    UserRepository userRepository;

    private static  final List<Object> admin_access = Arrays.asList(RoleName.ROLE_USER);
    //2) get role of logged in user;
       @GetMapping("/role")
    public List<SimpleGrantedAuthority> getRoleByLoggedInUser(Principal principal){

           Set<Role> roles = getLoggedInUser(principal).getRoles();
        List<SimpleGrantedAuthority> assignRole= roles.stream().map(role -> new
                SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
          // System.out.println(assignRole);
           String s = assignRole.stream().map(m -> m.getAuthority().equals(RoleName.ROLE_ADMIN)).toString();

           String collect = assignRole.stream().map(m -> m.getAuthority()).toString();
           System.out.println(">>>>" + collect);
           Boolean B =  collect.equals("ROLE_ADMIN");
           System.out.println("<<<<"+ B);
           return  assignRole;

       }


}
