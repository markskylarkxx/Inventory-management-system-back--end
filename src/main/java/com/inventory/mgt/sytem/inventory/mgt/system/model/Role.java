package com.inventory.mgt.sytem.inventory.mgt.system.model;

import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long roleId;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}
