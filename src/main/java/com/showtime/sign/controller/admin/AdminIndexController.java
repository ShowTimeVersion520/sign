package com.showtime.sign.controller.admin;

import com.github.pagehelper.Page;

import com.showtime.sign.model.base.ViewObject;
import com.showtime.sign.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员可以录入信息，删除信息
 */
@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class AdminIndexController {


    @Autowired
    private AdminService adminService;

    @GetMapping(value = {"/index"})
    public String index(){
        return "admin/index";
    }

    @GetMapping(value = {"/change/password"})
    public String IntoChangePassword(){
        return "admin/changePassword";
    }

    @ResponseBody
    @PostMapping(value = {"/change/password"})
    public String updatePassword(@Param("password") String password){
        adminService.updatePassword(password);
        return "密码修改成功";
    }


//    @GetMapping(value = {"/index"})
//    public String index(@RequestParam(value = "categoryId", required = false, defaultValue = "-1") Long categoryId,
//                        @RequestParam(value = "page",required = false, defaultValue = "1") Integer page,
//                        @RequestParam(value = "rows",required = false, defaultValue = "10") Integer rows,
//                        Model model) {
//        List<ViewObject> vos = new ArrayList<>();
//        Page<Post> posts = null;
//        if(categoryId.equals(-1)){
//            posts = postService.getAll(page, rows, new Post());
//        }else{
//            Post post = new Post();
//            post.setCategoryId(categoryId);
//            posts = postService.getAll(page, rows, post);
//        }
//
//        List<Categories> categories = categoriesService.findAll();
//        for(Post post:posts){
//            ViewObject vo = new ViewObject();
//            vo.set("post", post);
//            for(Categories category:categories){
//                if(category.getId().equals(post.getCategoryId())){
//                    vo.set("category", category.getName());
//                }
//            }
//            vos.add(vo);
//        }
//        model.addAttribute("vos", vos);
//        model.addAttribute("admin", adminService.getAdmin());
//        model.addAttribute("categories", categories);
//
//        log.info("currentPage = {}, size = {}, pageNum = {}", page, rows, posts.getPageNum());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("size", rows);
//        model.addAttribute("pageNum", posts.getPageNum());
//        return "admin/index";
//    }
}
