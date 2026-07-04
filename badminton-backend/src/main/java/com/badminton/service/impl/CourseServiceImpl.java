package com.badminton.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.badminton.common.BusinessException;
import com.badminton.common.Constants;
import com.badminton.dto.CourseCreateRequest;
import com.badminton.entity.*;
import com.badminton.mapper.CourseMapper;
import com.badminton.mapper.CourseStudentMapper;
import com.badminton.mapper.UserMapper;
import com.badminton.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Course> getCourseList(Integer pageNum, Integer pageSize, Integer level, Integer status) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        if (level != null) {
            wrapper.eq(Course::getLevel, level);
        }
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        } else {
            wrapper.eq(Course::getStatus, Constants.COURSE_STATUS_AVAILABLE);
        }

        // 关联教练信息
        wrapper.orderByDesc(Course::getCreateTime);
        Page<Course> result = this.page(page, wrapper);

        // 填充教练信息
        result.getRecords().forEach(course -> {
            User coach = userMapper.selectById(course.getCoachId());
            course.setCoach(coach);
        });

        return result;
    }

    @Override
    public Course getCourseById(Long id) {
        Course course = this.getById(id);
        if (course != null) {
            User coach = userMapper.selectById(course.getCoachId());
            if (coach != null) {
                coach.setPassword(null);
            }
            course.setCoach(coach);
        }
        return course;
    }

    @Override
    public List<Course> getCoachCourses(Long coachId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCoachId, coachId);
        wrapper.orderByDesc(Course::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public Course addCourse(Long coachId, CourseCreateRequest request) {
        Course course = new Course();
        course.setCoachId(coachId);
        course.setCourtId(request.getCourtId());
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setMaxStudents(request.getMaxStudents());
        course.setCurrentNum(0);
        course.setDuration(request.getDuration());
        course.setSchedule(request.getSchedule());
        course.setLevel(request.getLevel());
        course.setStatus(Constants.COURSE_STATUS_AVAILABLE);
        course.setImageUrl(request.getImageUrl());
        course.setLessonCount(request.getLessonCount());
        course.setCurriculum(request.getCurriculum());
        course.setTargetAudience(request.getTargetAudience());
        course.setGraduationStandard(request.getGraduationStandard());
        course.setTrainingFocus(request.getTrainingFocus());
        course.setFrequency(request.getFrequency());

        this.save(course);
        return this.getCourseById(course.getId());
    }

    @Override
    @Transactional
    public Course updateCourse(Long coachId, Course course) {
        Course existingCourse = this.getById(course.getId());
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }
        if (!existingCourse.getCoachId().equals(coachId)) {
            throw new BusinessException("无权修改该课程");
        }

        this.updateById(course);
        return this.getCourseById(course.getId());
    }

    @Override
    @Transactional
    public void deleteCourse(Long coachId, Long id) {
        Course course = this.getById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (!course.getCoachId().equals(coachId)) {
            throw new BusinessException("无权删除该课程");
        }
        if (course.getCurrentNum() > 0) {
            throw new BusinessException("该课程已有学员报名，无法删除");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public CourseStudent enrollCourse(Long userId, Long courseId) {
        Course course = this.getById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (course.getStatus() != Constants.COURSE_STATUS_AVAILABLE) {
            throw new BusinessException("该课程已下架");
        }
        if (course.getCurrentNum() >= course.getMaxStudents()) {
            throw new BusinessException("课程已满员");
        }

        // 检查是否已报名
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getUserId, userId)
                .ne(CourseStudent::getStatus, Constants.ENROLL_STATUS_CANCELLED);
        if (courseStudentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已报名该课程");
        }

        // 创建报名记录
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourseId(courseId);
        courseStudent.setUserId(userId);
        courseStudent.setStatus(Constants.ENROLL_STATUS_ENROLLED);
        courseStudentMapper.insert(courseStudent);

        // 更新课程报名人数
        course.setCurrentNum(course.getCurrentNum() + 1);
        this.updateById(course);

        return courseStudent;
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getUserId, userId)
                .eq(CourseStudent::getStatus, Constants.ENROLL_STATUS_ENROLLED);
        CourseStudent courseStudent = courseStudentMapper.selectOne(wrapper);

        if (courseStudent == null) {
            throw new BusinessException("您未报名该课程");
        }

        courseStudent.setStatus(Constants.ENROLL_STATUS_CANCELLED);
        courseStudentMapper.updateById(courseStudent);

        // 更新课程报名人数
        Course course = this.getById(courseId);
        course.setCurrentNum(Math.max(0, course.getCurrentNum() - 1));
        this.updateById(course);
    }

    @Override
    public Page<Course> getUserEnrolledCourses(Long userId, Integer pageNum, Integer pageSize) {
        // 查询用户报名的课程ID
        LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(CourseStudent::getUserId, userId)
                .eq(CourseStudent::getStatus, Constants.ENROLL_STATUS_ENROLLED);
        List<CourseStudent> courseStudents = courseStudentMapper.selectList(csWrapper);

        if (courseStudents.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 查询课程
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Course::getId, courseStudents.stream().map(CourseStudent::getCourseId).toList());
        Page<Course> result = this.page(page, wrapper);

        // 填充教练信息
        result.getRecords().forEach(course -> {
            User coach = userMapper.selectById(course.getCoachId());
            course.setCoach(coach);
        });

        return result;
    }

    @Override
    public List<CourseStudent> getCourseStudents(Long courseId) {
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseStudent::getCourseId, courseId);
        List<CourseStudent> students = courseStudentMapper.selectList(wrapper);

        students.forEach(cs -> {
            User user = userMapper.selectById(cs.getUserId());
            cs.setUser(user);
        });

        return students;
    }

    @Override
    public Page<CourseStudent> getCoachStudents(Long coachId, Integer pageNum, Integer pageSize) {
        // 获取教练的课程ID列表
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getCoachId, coachId);
        List<Course> courses = this.list(courseWrapper);

        if (courses.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        List<Long> courseIds = courses.stream().map(Course::getId).toList();

        // 获取这些课程的学员
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CourseStudent::getCourseId, courseIds);
        wrapper.eq(CourseStudent::getStatus, 1);
        Page<CourseStudent> page = courseStudentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 填充用户信息
        page.getRecords().forEach(cs -> {
            User user = userMapper.selectById(cs.getUserId());
            cs.setUser(user);
            Course course = this.getById(cs.getCourseId());
            cs.setCourse(course);
        });

        return page;
    }
}
