import cookies from 'react-cookies'

export default (current, action) => {
    switch (action.type) {
        case 'UPDATE': 
            let cart = cookies.load('cart') || {}; 
            let totalAmount = 0;
            let totalQuantity = 0;
            
            for (let c of Object.values(cart)) {
                // Khóa học mặc định quantity là 1
                let q = c.quantity || 1; 
                totalQuantity += q;
                totalAmount += q * c.price;
            }

            return {
                "totalQuantity": totalQuantity,
                "totalAmount": totalAmount
            }
            
        case 'PAID':
            cookies.remove('cart');
            return {
                "totalQuantity": 0,
                "totalAmount": 0
            }
            
        default:
            return current;
    }
}