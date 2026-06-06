import { useContext, useEffect, useState } from "react";
import { Badge, Button, Container, Form, Nav, Navbar, NavDropdown } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import { Link, useNavigate } from "react-router-dom";
import { MyCartContext, MyUserContext } from "../configs/Contexts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartShopping } from "@fortawesome/free-solid-svg-icons";
import cookies from "react-cookies"; 

const Header = () => {
    const [categories, setCategories] = useState([]);
    const [kw, setKw] = useState("");
    const nav = useNavigate();
    
    const [user, dispatch] = useContext(MyUserContext);
    const [cartCounter] = useContext(MyCartContext) || [{ totalQuantity: 0 }];
    const [cartQuantity, setCartQuantity] = useState(0);

    // KIỂM TRA ROLE GIẢNG VIÊN
    const isInstructor = user && user.role === 'INSTRUCTOR';

    useEffect(() => {
        const loadCates = async () => {
            try {
                let res = await Apis.get(endpoints['categories']);
                setCategories(res.data);
            } catch (err) {
                console.error("Lỗi khi tải danh mục: ", err);
            }
        };
        // Giảng viên không cần tải danh mục khóa học để tối ưu hiệu suất
        if (!isInstructor) {
            loadCates();
        }
    }, [isInstructor]);

    useEffect(() => {
        if (user && !isInstructor) {
            const cartCookieName = `cart_${user.username}`;
            let currentCart = cookies.load(cartCookieName) || {};
            setCartQuantity(Object.keys(currentCart).length);
        } else {
            setCartQuantity(0);
        }
    }, [user, cartCounter, isInstructor]); 
    
    const handleLogout = () => {
        dispatch({ "type": "LOGOUT" });
        nav("/"); 
    };

    return (
        <Navbar expand="lg" className="navbar-dark bg-dark-blue shadow-sm"> 
            <Container>
                <Navbar.Brand as={Link} to={isInstructor ? "/instructor" : "/"}>
                    <i className="bi bi-book-half me-2"></i>E-Learning
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                
                <Navbar.Collapse id="basic-navbar-nav">

                    <Nav className="me-auto">
                        {isInstructor ? (

                            <>
                                <Link className="nav-link" to="/instructor">
                                    <i className="bi bi-speedometer2 me-1"></i> Thống kê
                                </Link>
                                <Link className="nav-link" to="/instructor/courses">
                                    <i className="bi bi-collection-play me-1"></i> Quản lý Khóa học
                                </Link>
                                <Link className="nav-link" to="/instructor/chats">
                                    Tin nhắn
                                </Link>
                            </>
                        ) : (

                            <>
                                <Link className="nav-link" to="/">Trang chủ</Link>
                                <Link className="nav-link" to="/courses">Khóa học</Link>
                                
                                <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                                    {categories.map(c => (
                                        <Link key={c.id} className="dropdown-item" to={`/courses?cateId=${c.id}`}>
                                            {c.name}
                                        </Link>
                                    ))}
                                </NavDropdown>
                                
                                {user && (
                                    <Link className="nav-link" to="/my-enrollments">Khóa học của tôi</Link>
                                    
                                )}
                                {user && (
                                    <Link className="nav-link" to="/my-chats">
                                        Tin nhắn
                                    </Link>
                                )}
                            </>
                        )}
                    </Nav>

                    <Nav className="align-items-center">
                        {!isInstructor && (
                            <Link className="nav-link me-3 d-flex align-items-center" to="/cart">
                                <FontAwesomeIcon icon={faCartShopping} /> 
                                <Badge bg="success" className="ms-1">{cartQuantity}</Badge>
                            </Link>
                        )}

                        {/* Login / Profile / Logout (Dùng chung) */}
                        {!user ? (
                            <>
                                <Link className="btn btn-outline-light me-2" to="/register">Đăng ký</Link>
                                <Link className="btn btn-success" to="/login">Đăng nhập</Link>
                            </>
                        ) : (
                            <>
                                <Link className="nav-link d-flex align-items-center" to="/profile">
                                    <img 
                                        src={user.avatar || 'https://via.placeholder.com/150'} 
                                        alt="avatar" 
                                        width="30" height="30" 
                                        className="rounded-circle me-2 border" 
                                        style={{ objectFit: 'cover' }} 
                                    />
                                    Chào {user.username}!
                                </Link>
                                <Button variant="danger" size="sm" className="ms-2" onClick={handleLogout}>
                                    Đăng xuất
                                </Button>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;