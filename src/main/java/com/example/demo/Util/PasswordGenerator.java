package com.example.demo.Util;
import java.security.SecureRandom;
public class PasswordGenerator {
    // Định nghĩa các ký tự có thể sử dụng trong mật khẩu

    // Hàm tạo mật khẩu ngẫu nhiên với độ dài cho trước và chứa ít nhất một ký tự chữ hoa, một ký tự chữ thường, một ký tự số và một ký tự đặc biệt
    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Tạo một danh sách ký tự bao gồm ít nhất một ký tự của mỗi loại
       final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final  String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        final String digits = "0123456789";
        final  String specialCharacters = "!@#$%^&*()-_=+";

        String allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;

        // Thêm ít nhất một ký tự của mỗi loại vào mật khẩu
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // Thêm các ký tự ngẫu nhiên còn lại cho đến khi đạt được độ dài mong muốn
        for (int i = 4; i < length; i++) {
            char randomChar = allCharacters.charAt(random.nextInt(allCharacters.length()));
            password.append(randomChar);
        }

        // Đảo trộn mật khẩu để ngăn chặn các ký tự được thêm vào theo thứ tự cố định
        shufflePassword(password);

        return password.toString();
    }

    // Hàm đảo trộn mật khẩu để tăng độ mạnh của mật khẩu ngẫu nhiên
    private static void shufflePassword(StringBuilder password) {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < password.length(); i++) {
            int randomIndex = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }
    }
}
