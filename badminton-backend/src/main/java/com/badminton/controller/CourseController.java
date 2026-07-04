package com.badminton.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.badminton.common.Result;
import com.badminton.dto.CourseCreateRequest;
import com.badminton.entity.Course;
import com.badminton.entity.CourseStudent;
import com.badminton.entity.User;
import com.badminton.mapper.UserMapper;
import com.badminton.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserMapper userMapper;

    /**
     * 获取课程列表
     */
    @GetMapping("/list")
    public Result<Page<Course>> getCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer status) {
        Page<Course> page = courseService.getCourseList(pageNum, pageSize, level, status);
        return Result.success(page);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Result.notFound();
        }
        return Result.success(course);
    }

    /**
     * 获取教练的课程列表
     */
    @GetMapping("/coach/{coachId}")
    public Result<List<Course>> getCoachCourses(@PathVariable Long coachId) {
        List<Course> courses = courseService.getCoachCourses(coachId);
        return Result.success(courses);
    }

    /**
     * 添加课程
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public Result<Course> addCourse(Authentication authentication,
                                     @Validated @RequestBody CourseCreateRequest request) {
        Long userId = (Long) authentication.getDetails();
        User user = userMapper.selectById(userId);
        if (user.getRole() != 2 && user.getRole() != 3) {
            return Result.forbidden();
        }
        Course course = courseService.addCourse(userId, request);
        return Result.success("添加成功", course);
    }

    /**
     * 更新课程
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public Result<Course> updateCourse(Authentication authentication,
                                         @Validated @RequestBody Course course) {
        Long userId = (Long) authentication.getDetails();
        Course result = courseService.updateCourse(userId, course);
        return Result.success("更新成功", result);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public Result<?> deleteCourse(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        courseService.deleteCourse(userId, id);
        return Result.success("删除成功");
    }

    /**
     * 报名课程
     */
    @PostMapping("/enroll/{id}")
    public Result<CourseStudent> enrollCourse(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        CourseStudent courseStudent = courseService.enrollCourse(userId, id);
        return Result.success("报名成功", courseStudent);
    }

    /**
     * 取消报名
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancelEnrollment(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        courseService.cancelEnrollment(userId, id);
        return Result.success("取消成功");
    }

    /**
     * 获取我的课程
     */
    @GetMapping("/my-courses")
    public Result<Page<Course>> getMyCourses(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getDetails();
        Page<Course> page = courseService.getUserEnrolledCourses(userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取课程报名学员列表
     */
    @GetMapping("/students/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public Result<List<CourseStudent>> getCourseStudents(@PathVariable Long id) {
        List<CourseStudent> students = courseService.getCourseStudents(id);
        return Result.success(students);
    }

    /**
     * 获取教练的所有学员
     */
    @GetMapping("/coach-students")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public Result<Page<CourseStudent>> getCoachStudents(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long coachId = (Long) authentication.getDetails();
        Page<CourseStudent> page = courseService.getCoachStudents(coachId, pageNum, pageSize);
        return Result.success(page);
    }
}
