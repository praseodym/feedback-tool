package ch.wisv.controller;

import ch.wisv.domain.course.Course;
import ch.wisv.domain.feedback.EducationFeedback;
import ch.wisv.service.CourseService;
import ch.wisv.service.EducationFeedbackService;
import ch.wisv.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;

/**
 * Controller for education feedback.
 *
 * Created by Tom on 29/04/2017.
 */
@Controller
@RequestMapping("/education")
public class EducationFeedbackController {
    /** JPA service for education feedback. */
    private EducationFeedbackService educationFeedbackService;
    /** JPA service for courses. */
    private CourseService courseService;
    /** Service for mail notifications. */
    private NotificationService notificationService;

    /**
     * Autowired constructor.
     */
    @Autowired
    public EducationFeedbackController(EducationFeedbackService educationFeedbackService, CourseService courseService, NotificationService notificationService) {
        this.educationFeedbackService = educationFeedbackService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    /**
     * Create new education feedback.
     */
    @RequestMapping("/create")
    public String create(Model model) {
        model.addAttribute("feedback", new EducationFeedback());
        model.addAttribute("courses", courseService.list());
        return "education/educationForm";
    }

    /**
     * Save new education feedback.
     */
    @Transactional
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("feedback") EducationFeedback educationFeedback,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Course course = courseService.get(educationFeedback.getCourseCode().toUpperCase());
        if (course == null) {
            model.addAttribute("courseCodeError", "");
            model.addAttribute("courses", courseService.list());
            return "education/educationForm";
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("courses", courseService.list());
            return "education/educationForm";
        }

        educationFeedback.setCourse(course);
        educationFeedbackService.save(educationFeedback);
        notificationService.sendNotifications(educationFeedback);
        redirectAttributes.addFlashAttribute("message", "Thanks! Your feedback has been submitted." +
                " If you filled in your email, you will find a copy of your feedback in your mail.");
        return "redirect:/education/create";
    }
}
