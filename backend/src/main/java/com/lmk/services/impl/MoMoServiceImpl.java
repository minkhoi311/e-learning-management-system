/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.services.MomoService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Acer
 */
@Service
public class MoMoServiceImpl implements MomoService{
    private static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String PARTNER_CODE = "MOMO";
    private static final String ACCESS_KEY = "F8BBA842ECF85";
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    
    // Tôi đã đổi redirectUrl về trang React của bạn thay vì 'myapp://'
    private static final String REDIRECT_URL = "http://localhost:3000/my-enrollments"; 
    private static final String IPN_URL = "https://litmus-cargo-persuader.ngrok-free.dev/backend/api/payments/webhook"; 

    public String createMoMoPayment(int orderId, long amount, String orderInfo) {
        try {
            String amountStr = String.valueOf(amount);
            String requestId = UUID.randomUUID().toString();
            // Tránh trùng mã đơn hàng khi test nhiều lần
            String orderIdMoMo = orderId + "_" + UUID.randomUUID().toString().substring(0, 8);
            String requestType = "captureWallet";
            String extraData = "";

            // 1. Tạo chuỗi ký thô (raw signature) - phải ĐÚNG thứ tự Alphabet
            String rawSignature = "accessKey=" + ACCESS_KEY
                    + "&amount=" + amountStr
                    + "&extraData=" + extraData
                    + "&ipnUrl=" + IPN_URL
                    + "&orderId=" + orderIdMoMo
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + PARTNER_CODE
                    + "&redirectUrl=" + REDIRECT_URL
                    + "&requestId=" + requestId
                    + "&requestType=" + requestType;

            // 2. Ký bằng HMAC-SHA256
            String signature = encodeHmacSHA256(rawSignature, SECRET_KEY);

            // 3. Chuẩn bị Body (tương đương với dictionary 'data' trong Python)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", PARTNER_CODE);
            requestBody.put("partnerName", "E-Learning Nhom 11");
            requestBody.put("storeId", "MomoTestStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amountStr);
            requestBody.put("orderId", orderIdMoMo);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", REDIRECT_URL);
            requestBody.put("ipnUrl", IPN_URL);
            requestBody.put("lang", "vi");
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);

            // 4. Gửi HTTP POST bằng RestTemplate (Tương đương requests.post)
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi đi và nhận JSON trả về
            ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, entity, Map.class);
            Map<String, Object> result = response.getBody();

            // 5. Kiểm tra và trả về payUrl
            if (result != null && result.containsKey("payUrl")) {
                return (String) result.get("payUrl");
            } else {
                System.out.println("Lỗi từ MoMo: " + result);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Lỗi kết nối hoặc mã hóa: " + e.getMessage());
            return null;
        }
    }

    // Hàm tiện ích để mã hóa HMAC-SHA256
    private String encodeHmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] bytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Chuyển byte array sang chuỗi Hex
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }
}
