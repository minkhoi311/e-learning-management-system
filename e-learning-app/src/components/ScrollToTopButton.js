import React, { useState, useEffect } from 'react';
import { Button } from 'react-bootstrap';

export default function ScrollToTopButton() {
  const [isVisible, setIsVisible] = useState(false);

  // Kiểm tra vị trí cuộn chuột
  const toggleVisibility = () => {
    if (window.pageYOffset > 300) {
      setIsVisible(true);
    } else {
      setIsVisible(false);
    }
  };

  // Hàm trượt mượt mà lên đầu trang
  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  };

  useEffect(() => {
    window.addEventListener("scroll", toggleVisibility);
    return () => window.removeEventListener("scroll", toggleVisibility);
  }, []);

  return (
    <>
      {isVisible && (
        <Button
          variant="warning" // Dùng màu warning cho đồng bộ giao diện ANQINKO
          onClick={scrollToTop}
          className="rounded-circle shadow border-0 d-flex align-items-center justify-content-center text-dark"
          style={{
            position: 'fixed',
            bottom: '40px',
            right: '40px',
            width: '50px',
            height: '50px',
            zIndex: 9999,
            opacity: 0.85,
            transition: 'all 0.3s ease-in-out',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.opacity = '1';
            e.currentTarget.style.transform = 'translateY(-5px)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.opacity = '0.85';
            e.currentTarget.style.transform = 'translateY(0)';
          }}
          title="Cuộn lên đầu trang"
        >
          <i className="bi bi-arrow-up fs-4 fw-bold"></i> 
        </Button>
      )}
    </>
  );
}