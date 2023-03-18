package com.inventory.mgt.sytem.inventory.mgt.system.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.inventory.mgt.sytem.inventory.mgt.system.model.order.Order;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
   @Column(name = "first_name", nullable = false)
   private String firstName;
   @Column(name = "last_name")
   private String lastName;
    @Column(name = "password")
    private String  password;
    private String email;
    private String phoneNo;
    @Column(name = "active")
    private  boolean active;
    @Column(name = "address")
    private String  userAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Character gender;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Transient
    private List<Order> orders;


    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
   // @JsonIgnore
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role>  roles = new HashSet<>() ;

    public User(String username,
                String firstName,
                String lastName,
                String password,
                String email,
                String phoneNo,
                String userAddress,
                Character gender) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phoneNo = phoneNo;
        this.userAddress = userAddress;
        this.gender = gender;
    }
    public User(String username,
                String firstName,
                String lastName,
                String email,
                String phoneNo,
                String userAddress,
                Character gender) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.userAddress = userAddress;
        this.gender = gender;
    }
    public User(String username, String firstName, String lastName, String password, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }


    public User(User user) {
    }

    public User(String firstName, String userEmail) {
    }
}
