import axios from "axios";
import cookies from 'react-cookies';

export const endpoints = {
    'categories': '/categories',
    'courses': '/courses',
    'course-details': (courseId) => `/courses/${courseId}`,
    'compare': '/courses/compare',
    

    'lessons': (courseId) => `/courses/${courseId}/lessons`,
    'lesson-details': (lessonId) => `/lessons/${lessonId}`,
    'comments': (lessonId) => `/lessons/${lessonId}/comments`,
    'addComment': (lessonId) => `/secure/lessons/${lessonId}/comments`,


    'add-course': '/secure/courses',                                 
    'update-course': (courseId) => `/secure/courses/${courseId}`,   
    'delete-course': (courseId) => `/secure/courses/${courseId}`,
    'instructor-students': (courseId) => `/secure/instructor/courses/${courseId}/students`,
    

    'register': '/users',
    'login': '/login',
    'profile': '/secure/profile',
    'change-password': '/secure/change-password',
    

    'enroll': (courseId) => `/secure/courses/${courseId}/enroll`,
    'my-enrollments': '/secure/enrollments',
    'pay': (enrollmentId) => `/secure/enrollments/${enrollmentId}/pay`,
    'check-enrollment': (courseId) => `/secure/enrollments/check/${courseId}`,
    'complete-lesson': (enrollmentId, lessonId) => `/secure/enrollments/${enrollmentId}/lessons/${lessonId}/complete`,
    

    'instructor-stats': '/secure/stats/overview',
    'my-chat-sessions': '/secure/chat/sessions',
    'get-chat-room': '/secure/chat/room',
}

export const authApis = () => {
    return axios.create({
        baseURL: "http://localhost:8080/backend/api/", 
        headers: {
            'Authorization': `Bearer ${cookies.load('token')}`,
        }
    })
}

export default axios.create({
    baseURL: "http://localhost:8080/backend/api/"
});