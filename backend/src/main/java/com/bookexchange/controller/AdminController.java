package com.bookexchange.controller;

import com.bookexchange.dto.BookResponse;
import com.bookexchange.dto.PageResponse;
import com.bookexchange.dto.StatsResponse;
import com.bookexchange.dto.UserResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.AdminService;
import com.bookexchange.service.BookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Every route under /api/admin is restricted to ROLE_ADMIN in SecurityConfig. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BookService bookService;

    public AdminController(AdminService adminService, BookService bookService) {
        this.adminService = adminService;
        this.bookService = bookService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return adminService.users();
    }

    @PutMapping("/users/{id}/block")
    public UserResponse toggleBlock(@AuthenticationPrincipal User admin, @PathVariable String id) {
        return adminService.toggleBlock(admin, id);
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return adminService.stats();
    }

    @GetMapping("/books")
    public PageResponse<BookResponse> books(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return bookService.adminList(page, size);
    }
}
