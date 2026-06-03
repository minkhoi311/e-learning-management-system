import axios from "axios";
import cookies from 'react-cookies';

export const endpoints = {
    'categories': '/categories',
    'courses': '/courses',
    'course-details': (courseId) => `/courses/${courseId}`,
    'compare': '/courses/compare',
    
    // Lessons & Comments
    'lessons': (courseId) => `/courses/${courseId}/lessons`,
    'lesson-details': (lessonId) => `/lessons/${lessonId}`,
    'comments': (lessonId) => `/lessons/${lessonId}/comments`,
    'addComment': (lessonId) => `/secure/lessons/${lessonId}/comments`,

    // Instructor - Quản lý khóa học
    'add-course': '/secure/courses',                                  // <-- MỚI THÊM
    'update-course': (courseId) => `/secure/courses/${courseId}`,     // <-- MỚI THÊM
    'delete-course': (courseId) => `/secure/courses/${courseId}`,     // <-- MỚI THÊM
    'instructor-students': (courseId) => `/secure/instructor/courses/${courseId}/students`,
    
    // Auth & User
    'register': '/users',
    'login': '/login',
    'profile': '/secure/profile',
    'change-password': '/secure/change-password',
    
    // Enrollments
    'enroll': (courseId) => `/secure/courses/${courseId}/enroll`,
    'my-enrollments': '/secure/enrollments',
    'pay': (enrollmentId) => `/secure/enrollments/${enrollmentId}/pay`,
    'check-enrollment': (courseId) => `/secure/enrollments/check/${courseId}`,
    'complete-lesson': (enrollmentId, lessonId) => `/secure/enrollments/${enrollmentId}/lessons/${lessonId}/complete`,
    
    // Stats
    'instructor-stats': '/secure/stats/overview',

    'get-chat-room': '/secure/chat/room',
}

export const authApis = () => {
    return axios.create({
        baseURL: "http://localhost:8080/backend/api/", 
        headers: {
            'Authorization': `Bearer ${cookies.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: "http://localhost:8080/backend/api/"
});