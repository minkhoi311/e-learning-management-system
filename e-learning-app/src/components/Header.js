import { useContext, useEffect, useState } from "react";
import { Badge, Button, Container, Form, Nav, Navbar, NavDropdown } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import { Link, useNavigate } from "react-router-dom";
import { MyCartContext, MyUserContext } from "../configs/Contexts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartShopping } from "@fortawesome/free-solid-svg-icons";
import cookies from "react-cookies"; // Nhớ import thư viện cookies nhé

const Header = () => {
    const [categories, setCategories] = useState([]);
    const [kw, setKw] = useState("");
    const nav = useNavigate();
    
    const [user, dispatch] = useContext(MyUserContext);
    const [cartCounter, ] = useContext(MyCartContext) || [{ totalQuantity: 0 }];
    
    // State lưu số lượng giỏ hàng thực tế của user hiện tại
    const [cartQuantity, setCartQuantity] = useState(0);

    const loadCates = async () => {
        try {
            let res = await Apis.get(endpoints['categories']);
            setCategories(res.data);
        } catch (err) {
            console.error("Lỗi khi tải danh mục: ", err);
        }
    }

    useEffect(() => {
        loadCates();
    }, []);
    useEffect(() => {
        if (user) {
            const cartCookieName = `cart_${user.username}`;
            let currentCart = cookies.load(cartCookieName) || {};
            setCartQuantity(Object.keys(currentCart).length);
        } else {
            setCartQuantity(0);
        }
    }, [user, cartCounter]); 
    
    const search = (e) => {
        e.preventDefault();
        nav(`/courses?kw=${kw}`); 
    }

    return (
        <Navbar expand="lg" className="navbar-dark bg-dark-blue"> 
            <Container>
                <Navbar.Brand as={Link} to="/">E-Learning Nhóm 11</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Link className="nav-link" to="/">Trang chủ</Link>
                        <Link className="nav-link" to="/courses">Khóa học</Link>
                        
                        <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                            {categories.map(c => {
                                const url = `/courses?cateId=${c.id}`;
                                return <Link key={c.id} className="dropdown-item" to={url}>{c.name}</Link>;
                            })}
                        </NavDropdown>
                        
                        {user !== null && (
                            <Link className="nav-link" to="/my-enrollments">Khóa học của tôi</Link>
                        )}
                    </Nav>

                    <Nav className="align-items-center">
                        <Link className="nav-link me-3 d-flex align-items-center" to="/cart">
                            {/* Hiển thị cartQuantity thay vì cartCounter.totalQuantity */}
                            <FontAwesomeIcon icon={faCartShopping} /> <Badge bg="success" className="ms-1">{cartQuantity}</Badge>
                        </Link>

                        {user === null ? (
                            <>
                                <Link className="btn btn-outline-light me-2" to="/register">Đăng ký</Link>
                                <Link className="btn btn-success" to="/login">Đăng nhập</Link>
                            </>
                        ) : (
                            <>
                                <Link className="nav-link d-flex align-items-center" to="/profile">
                                    <img src={user.avatar || 'https://via.placeholder.com/150'} 
                                         alt="avatar" 
                                         width="30" 
                                         height="30" 
                                         className="rounded-circle me-2 border" 
                                         style={{ objectFit: 'cover' }} />
                                    Chào {user.username}!
                                </Link>
                                <Button variant="danger" size="sm" className="ms-2" onClick={() => {
                                    dispatch({"type": "LOGOUT"});
                                    nav("/"); // Đăng xuất xong đá về trang chủ cho an toàn
                                }}>
                                    Đăng xuất
                                </Button>
                            </>
                        )}
                    </Nav>

                    <Form className="d-flex ms-lg-3 mt-3 mt-lg-0" onSubmit={search}>
                        <Form.Control
                            type="search"
                            value={kw}
                            onChange={e => setKw(e.target.value)}
                            placeholder="Tìm khóa học..."
                            className="me-2"
                        />
                        <Button type="submit" variant="success">Tìm</Button>
                    </Form>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;