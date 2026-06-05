
function deleteCourse(url, buttonElement) {
    if (confirm("Bạn có chắc chắn muốn xóa khóa học này không? Hành động này không thể hoàn tác!")) {
        fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
                .then(response => {
                    if (response.ok) {
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
            body: JSON.stringify({"id": userId})
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
                            alert("Duyệt thất bại! Có lỗi xảy ra.");
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("Lỗi kết nối đến máy chủ API!");
                });
    }
}

let revenueChart = null;
function loadMonthlyRevenue(year, apiUrl) {
    fetch(`${apiUrl}?year=${year}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => {
        if (!response.ok) throw new Error("Lỗi xác thực hoặc máy chủ!");
        return response.json();
    })
    .then(data => {
        const labels = data.map(item => 'Tháng ' + item.month);
        const revenues = data.map(item => item.revenue);

        if (revenueChart) {
            revenueChart.destroy();
        }

        const ctx = document.getElementById('revenueChart');
        if (!ctx) return;

        revenueChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: revenues,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: { scales: { y: { beginAtZero: true } } }
        });
    })
    .catch(error => {
        console.error('Lỗi khi tải dữ liệu biểu đồ:', error);
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById('yearSelect');
    if (yearSelect) {
        const apiUrl = '/backend/api/stats/monthly'; 
        
        loadMonthlyRevenue(yearSelect.value, apiUrl);
        
        yearSelect.addEventListener('change', function () {
            loadMonthlyRevenue(this.value, apiUrl);
        });
    }
});