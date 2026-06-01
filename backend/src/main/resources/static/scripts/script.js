function deleteCourse(url, buttonElement) {
    alert(url);
    if (confirm("Bạn có chắc chắn muốn xóa khóa học này không? Hành động này không thể hoàn tác!")) {
   
        fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
                // 'Authorization': 'Bearer ' + localStorage.getItem('access_token')
            }
        })
        .then(response => {
            if (response.ok) { // Nếu API trả về 204 No Content hoặc 200 OK
                // Tìm dòng (thẻ <tr>) chứa nút bấm này và xóa nó khỏi DOM
                const row = buttonElement.closest('tr');
                row.remove();
                 alert("Xóa khóa học thành công!");
            } else {
                alert("Xóa thất bại! Không tìm thấy khóa học hoặc có lỗi máy chủ.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Lỗi kết nối đến máy chủ API!");
        });
    }
}


function approveInstructor(url, userId, buttonElement) {
    if (confirm("Xác nhận duyệt giảng viên này?")) {
        fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ "id": userId }) 
        })
        .then(response => {
            if (response.ok) {
                const row = buttonElement.closest('tr');
                if (row) {
                    const badge = row.querySelector('.bg-warning');
                    if (badge) {
                        badge.className = 'badge bg-success';
                        badge.innerHTML = '<i class="bi bi-check-circle"></i> GV (Đã duyệt)';
                    }
                    
                    buttonElement.style.transition = "opacity 0.3s ease";
                    buttonElement.style.opacity = 0;
                    setTimeout(() => buttonElement.remove(), 300);
                }
            } else {
                response.json().then(data => {
                    alert("Duyệt thất bại: " + (data.message || "Lỗi máy chủ"));
                }).catch(() => {
                    alert("Duyệt thất bại! Có lỗi xảy ra (Sai đường dẫn hoặc mất mạng).");
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Lỗi kết nối đến máy chủ API!");
        });
    }
}
