package com.ch.controller.admin;

import com.ch.domain.course.Course;
import com.ch.domain.course.courseloader.CourseLoader;
import com.ch.domain.course.courseloader.CoursesString;
import com.ch.domain.feedback.EducationFeedback;
import com.ch.service.CourseService;
import com.ch.service.EducationFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by Tom on 30/04/2017.
 */
@Controller
@RequestMapping("/admin/course")
public class AdminCourseController {
    private CourseService courseService;
    private EducationFeedbackService educationFeedbackService;
    private CourseLoader courseLoader;

    @Autowired
    public AdminCourseController(CourseService courseService, EducationFeedbackService educationFeedbackService, CourseLoader courseLoader) {
        this.courseService = courseService;
        this.educationFeedbackService = educationFeedbackService;
        this.courseLoader = courseLoader;
    }

    @RequestMapping("/{courseCode}")
    public String getCourse(@PathVariable String courseCode, Model model) {
        courseCode = courseCode.toUpperCase();
        Course course = courseService.get(courseCode);
        List<EducationFeedback> feedbackOnCourse = educationFeedbackService.getCourseFeedback(course.getId());
        model.addAttribute("course", course);
        model.addAttribute("feedbackOnCourse", feedbackOnCourse);
        model.addAttribute("link", "admin/education/");

        return "admin/course/view";
    }

    @RequestMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("courses", new CoursesString());
        return "admin/course/settings";
    }

    @RequestMapping("/settings/load")
    public String load(@ModelAttribute("courses") CoursesString courses, RedirectAttributes redirectAttributes) {
        courseLoader.load(courses.getCourseLoadList());
        redirectAttributes.addFlashAttribute("message", "The course(s) are uploaded to the system!");
        return "redirect:/admin/course/settings";
    }

    @RequestMapping("/settings/delete")
    public String delete(@ModelAttribute("courses") CoursesString courses, RedirectAttributes redirectAttributes) {
        courseService.delete(courses.getCourseDeleteList());
        redirectAttributes.addFlashAttribute("message", "The course(s) are deleted from the system!");
        return "redirect:/admin/course/settings";
    }
}
