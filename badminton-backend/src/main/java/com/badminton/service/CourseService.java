package com.badminton.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.badminton.dto.CourseCreateRequest;
import com.badminton.entity.Course;
import com.badminton.entity.CourseStudent;

import java.util.List;
import java.util.Map;

public interface CourseService extends IService<Course> {

    /**
     * 获取课程列表
     */
    Page<Course> getCourseList(Integer pageNum, Integer pageSize, Integer level, Integer status);

    /**
     * 获取课程详情
     */
    Course getCourseById(Long id);

    /**
     * 获取教练的课程列表
     */
    List<Course> getCoachCourses(Long coachId);

    /**
     * 添加课程
     */
    Course addCourse(Long coachId, CourseCreateRequest request);

    /**
     * 更新课程
     */
    Course updateCourse(Long coachId, Course course);

    /**
     * 删除课程
     */
    void deleteCourse(Long coachId, Long id);

    /**
     * 报名课程
     */
    CourseStudent enrollCourse(Long userId, Long courseId);

    /**
     * 取消报名
     */
    void cancelEnrollment(Long userId, Long courseId);

    /**
     * 获取用户已报名的课程
     */
    Page<Course> getUserEnrolledCourses(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取课程报名学员列表
     */
    List<CourseStudent> getCourseStudents(Long courseId);

    /**
     * 获取教练的所有学员
     */
    Page<CourseStudent> getCoachStudents(Long coachId, Integer pageNum, Integer pageSize);
}
