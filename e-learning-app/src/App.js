import React, { useReducer } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import cookies from 'react-cookies';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css'; 
import './styles/style.css'; 

import { MyUserContext, MyCartContext } from './configs/Contexts';
import MyUserReducer from './reducers/MyUserReducer';
import MyCartReducer from './reducers/MyCartReducer';

import Header from './components/Header';
import Footer from './components/Footer'; 
import ScrollToTopButton from './components/ScrollToTopButton';

import Courses from './screens/Course/Courses';
import CompareCourses from './screens/Course/CompareCourses';
import CourseDetail from './screens/Course/CourseDetail'; 
import LessonDetail from './screens/Course/LessonDetail';
import Login from './screens/User/Login'; 
import Register from './screens/User/Register';
import Cart from './screens/Cart/Cart'; 
import MyEnrollments from './screens/User/MyEnrollments'; 
import InstructorDashboard from './screens/Instructor/InstructorDashboard';
import Profile from './screens/User/Profile';
import ChangePassword from './screens/User/ChangePassword';

import InstructorCourses from './screens/Instructor/InstructorCourses';
import CourseForm from './screens/Instructor/CourseForm';
import InstructorStudents from './screens/Instructor/InstructorStudents';


// Placeholder (Còn lại phần Trang chủ và Giảng viên)
const Home = () => <div className="container mt-4 text-center"><h2>Trang chủ</h2></div>;
const Instructors = () => <div className="container mt-4 text-center"><h2>Giảng viên</h2></div>;

const App = () => {
    const [user, dispatch] = useReducer(MyUserReducer, cookies.load('user') || null);

    const initCartState = () => {
        let currentUser = cookies.load('user');
        if (!currentUser) {
            return { totalQuantity: 0, totalAmount: 0 };
        }
        let cartCookieName = `cart_${currentUser.username}`;
        let cart = cookies.load(cartCookieName) || {};
        let totalQuantity = 0;
        let totalAmount = 0;
        
        for (let c of Object.values(cart)) {
            let q = c.quantity || 1;
            totalQuantity += q;
            totalAmount += q * c.price;
        }
        return { totalQuantity, totalAmount };
    };
    const [cartCounter, cartDispatch] = useReducer(MyCartReducer, initCartState());

    return (
        <MyUserContext.Provider value={[user, dispatch]}>
            <MyCartContext.Provider value={[cartCounter, cartDispatch]}>
                
                <BrowserRouter>
                    <div className="d-flex flex-column min-vh-100">
                        <Header />
                        <Routes>
                            <Route path="/" element={<Home />} />
                            
                            <Route path="/courses" element={<Courses />} />
                            <Route path="/courses/:courseId" element={<CourseDetail />} /> 
                            <Route path="/compare" element={<CompareCourses />} /> 
                            <Route path="/lessons/:lessonId" element={<LessonDetail />} /> 

                            <Route path="/instructors" element={<Instructors />} />
                            <Route path="/instructor/courses" element={<InstructorCourses />} />
                            <Route path="/instructor/courses/add" element={<CourseForm />} />
                            <Route path="/instructor/courses/edit/:courseId" element={<CourseForm />} />
                            <Route path="/instructor/courses/:courseId/students" element={<InstructorStudents />} />

                            <Route path="/login" element={<Login />} /> 
                            <Route path="/register" element={<Register />} />
                            <Route path="/cart" element={<Cart />} />
                            <Route path="/instructor" element={<InstructorDashboard />} />
                            <Route path="/my-enrollments" element={<MyEnrollments />} />
                            <Route path="/profile" element={<Profile />} />
                            <Route path="/change-password" element={<ChangePassword />} />
                        </Routes>
                        
                        <Footer />
                    </div>

                    <ScrollToTopButton />
                </BrowserRouter>

            </MyCartContext.Provider>
        </MyUserContext.Provider>
    );
}

export default App;