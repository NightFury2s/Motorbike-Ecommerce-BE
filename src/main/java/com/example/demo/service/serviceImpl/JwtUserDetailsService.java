package com.example.demo.service.serviceImpl;

import com.example.demo.Util.CheckEmptys;
import com.example.demo.config.JwtTokenUtil;
import com.example.demo.model.Dto.*;
import com.example.demo.model.entity.DAOUser;
import com.example.demo.model.entity.Role;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final Messenger messenger;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    public JwtUserDetailsService(Messenger messenger, UserRepository userRepository, RoleRepository roleRepository) {
        this.messenger = messenger;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DAOUser user = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Create a list to store user's authorities (roles)
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add roles to authorities
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole()));
        // You can add more roles as needed

        // Create UserDetails object with roles
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public UserDTO getUserDto(String username) {
        return new UserDTO(userRepository.findByUsername(username));
    }

    private boolean isInfo(UserRequestDto userRequestDto) {
        return CheckEmptys.checkEmpty(userRequestDto.getUsername())
                || CheckEmptys.checkEmpty(userRequestDto.getPassword())
                || CheckEmptys.checkEmpty(userRequestDto.getAddress())
                || CheckEmptys.checkEmpty(userRequestDto.getPhoneNumber())
                || CheckEmptys.checkEmpty(userRequestDto.getEmail())
                || CheckEmptys.checkEmpty(userRequestDto.getFullName());
    }

    //kiểm tra điều kiện
    private ResponseEntity<?> validateRegisterInfo(UserRequestDto userRequestDto) {
        //check thông tin người dùng có để trống không
        if (isInfo(userRequestDto)) {
            messenger.setMessenger("Vui lòng nhập đầy đủ thông tin");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (userRequestDto.getFullName().length() < 6) {
            messenger.setMessenger("Tên đầy đủ phải ít nhất 6 kí tự");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            messenger.setMessenger("Email đã tồn tại");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        String regex = "^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*@gmail.com$";
        if (!Pattern.matches(regex, userRequestDto.getEmail())) {
            messenger.setMessenger("Email không hợp lệ ");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            messenger.setMessenger("Số điện thoại đã tồn tại");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (userRequestDto.getPhoneNumber().length() < 10) {
            messenger.setMessenger("Số điện thoại phải 10 Số");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (!userRequestDto.getUsername().matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            messenger.setMessenger("Tên đăng nhập không được bắt đầu bằng ký tự đặc biệt hoặc số");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            messenger.setMessenger("Tên đăng nhập đã tồn tại");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }

        if (userRequestDto.getUsername().length() < 6
                || userRequestDto.getPassword().length() < 6) {
            messenger.setMessenger("Tài khoản hoặc mật khẩu phải ít nhất 6 kí tự");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        if (!userRequestDto.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+]).{6,}$")) {
            messenger.setMessenger(" Mật khẩu bắt buộc gồm số, chữ cái thường, chữ cái hoa, ký tự đặc biệt");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    //dang ki
    public ResponseEntity<?> save(UserRequestDto userRequestDto) {

        try {
            // check thông tin trước khi đăng kí tk
            ResponseEntity<?> validationResponse = validateRegisterInfo(userRequestDto);
            if (validationResponse != null) {
                return validationResponse;
            }

            //tạo 1 User để luu vào csdl
            DAOUser daoUser = new DAOUser();
            // gán thông tin cho user lấy từ dto
            daoUser.setUsername(userRequestDto.getUsername());
            daoUser.setPassword(bcryptEncoder.encode(userRequestDto.getPassword()));
            daoUser.setAddress(userRequestDto.getAddress());
            daoUser.setEmail(userRequestDto.getEmail());
            daoUser.setFullName(userRequestDto.getFullName());
            daoUser.setPhoneNumber(userRequestDto.getPhoneNumber());

            //2 mac dinh laf user
            Role role = roleRepository.findById(2L).orElse(null);
            daoUser.setRole(role);
            //lưu
            userRepository.save(daoUser);
            log.info("Tạo tài khoản thành công, " + "username: " + daoUser.getUsername());
            messenger.setMessenger("Tạo tài khoản thành công");
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred ");
            messenger.setMessenger("Đã xãy ra lỗi khi thêm tài khoản.))");
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }
    }

    // hàm check tk mk
    private ResponseEntity<?> validateLoginInfo(JwtRequest authenticationRequest) {
        if (CheckEmptys.checkEmpty(authenticationRequest.getUsername())
                && CheckEmptys.checkEmpty(authenticationRequest.getPassword())) {
            messenger.setMessenger("Vui lòng nhập tài khoản và mật khẩu");
            return new ResponseEntity<>(messenger, HttpStatus.UNAUTHORIZED);
        }
        if (CheckEmptys.checkEmpty(authenticationRequest.getUsername())
        ) {
            messenger.setMessenger("Vui lòng nhập tên đăng nhập");
            return new ResponseEntity<>(messenger, HttpStatus.UNAUTHORIZED);
        }
        if (CheckEmptys.checkEmpty(authenticationRequest.getPassword())
        ) {
            messenger.setMessenger("Vui lòng nhập mật khẩu");
            return new ResponseEntity<>(messenger, HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    //login
    public ResponseEntity<?> login(JwtRequest authenticationRequest) throws Exception {
        try {
            //check username password có hợp lệ không
            ResponseEntity<?> validationResponse = validateLoginInfo(authenticationRequest);
            if (validationResponse != null) {
                return validationResponse;
            }
            //xác thực thông tin đăng nhập
            authenticate(authenticationRequest.getUsername().trim(), authenticationRequest.getPassword());

            //tạo 1 userDetails
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            // tạo token
            final String token = jwtTokenUtil.generateToken(userDetails, userDetailsService.getUserDto(authenticationRequest.getUsername()));

            // lâấy thông tin ueser trừ mật khẩu
            DAOUser daoUser = userRepository.findByUsername(authenticationRequest.getUsername());

            log.info(authenticationRequest.getUsername() + " đăng nhập");
            //tra về token
            return ResponseEntity.ok(new JwtResponse(token, daoUser));
        } catch (Exception e) {
            messenger.setMessenger("Sai tài khoản hoặc mật khẩu");
            return new ResponseEntity<>(messenger, HttpStatus.UNAUTHORIZED);
        }
    }

    // xác thuực người dùng
    private void authenticate(String username, String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // găắn token chứa tt vào SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
