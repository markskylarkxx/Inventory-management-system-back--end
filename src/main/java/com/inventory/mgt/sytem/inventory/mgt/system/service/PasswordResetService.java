package com.inventory.mgt.sytem.inventory.mgt.system.service;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.ResetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;

public interface PasswordResetService {
    String forgotPass(User user);

    String resetPassword(ResetPassword resetPassword);
}
