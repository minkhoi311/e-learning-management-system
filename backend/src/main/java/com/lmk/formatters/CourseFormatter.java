package com.lmk.formatters;

import com.lmk.pojo.Course;
import org.springframework.format.Formatter;
import java.text.ParseException;
import java.util.Locale;

public class CourseFormatter implements Formatter<Course> {
    @Override
    public String print(Course course, Locale locale) {
        return String.valueOf(course.getId());
    }

    @Override
    public Course parse(String courseId, Locale locale) throws ParseException {
        Course c = new Course();
        c.setId(Integer.valueOf(courseId));
        return c;
    }
}