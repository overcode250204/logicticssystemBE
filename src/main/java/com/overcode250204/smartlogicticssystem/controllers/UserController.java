package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseCrudController<UserDTO, Long> {

    private final IUserService userService;

    protected UserController(IUserService service, IUserService userService) {
        super(service);
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUser() {
        List<UserDTO> response = userService.getAllUserDTO();
        return success(response, "Get All User Success");
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Boolean isActive) {
        List<UserDTO> response = userService.searchWithConditions(keyword, roleId, isActive);
        return success(response, "Search successful");
    }


}
