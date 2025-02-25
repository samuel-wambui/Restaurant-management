package HotelManagement.Auth.user;


import HotelManagement.Auth.dto.UserDTO;
import HotelManagement.Auth.dto.UserRoleDTO;
import HotelManagement.Departments.DepartmentEntity;
import HotelManagement.Departments.DepartmentRepo;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.roles.Role;
import HotelManagement.roles.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSender emailSender;
    @Autowired
    private DepartmentRepo departmentRepo;




    public User saveUser(UserDTO userDTO) {
        if (userRepository.existsByPhoneNumberAndDeletedFlag(userDTO.getPhoneNumber(), "N")) {
           throw new RuntimeException("Phone number already exists");

        }

        if (userRepository.existsByEmailAndDeletedFlag(userDTO.getEmail(), "N")) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setVerificationCode(generateCode(6));
        user.setVerificationTime(LocalDateTime.now());
        user.setUsername(generateUserName(userDTO));


        Set<Long> roleIds = userDTO.getRoleIds();
        List<Role> roles = roleRepository.findAllById(roleIds);

        if (roleIds.isEmpty() || roles.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER");
            user.setRole(Collections.singletonList(defaultRole));
        } else {
            user.setRole(roles);
        }

        userRepository.save(user);


        String toEmail = user.getEmail();
        String text = "Hello " + user.getFirstName() + ", your verification code is " + user.getVerificationCode() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, "Email Verification", text);
        return userRepository.save(user);

    }

    public User assignDepartments(Long userId, List<Long> departmentIds) {
        // Retrieve the user by ID
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        User user = optionalUser.get();

        // Prepare a collection to hold the departments to be assigned
        Set<DepartmentEntity> departments = new HashSet<>();

        // Process each departmentId
        for (Long departmentId : departmentIds) {
            Optional<DepartmentEntity> optionalDepartment = departmentRepo.findById(departmentId);

            // If the department is not found OR is marked as deleted, throw an exception.
            if (!optionalDepartment.isPresent() || optionalDepartment.get().isDeleted()) {
                // Use the department name if available; otherwise, fallback to the department ID.
                String deptIdentifier = optionalDepartment.map(DepartmentEntity::getDepartmentName)
                        .orElse("with ID: " + departmentId);
                throw new EntityNotFoundException("Department " + deptIdentifier + " is not found or is deleted");
            }

            // Add the valid department to the collection
            departments.add(optionalDepartment.get());
        }

        // Assign the departments to the user (assuming User has a Set<DepartmentEntity> departments)
        user.setDepartments(departments);

        // Save and return the updated user
        return userRepository.save(user);
    }


    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }


    public User assignRole(UserRoleDTO userRoleDTO) {
        Optional<User> optionalEmployee = userRepository.findById(userRoleDTO.getEmployeeId());
        if (!optionalEmployee.isPresent()) {
            throw new RuntimeException("Employee not found with ID: " + userRoleDTO.getEmployeeId());
        }
        User user = optionalEmployee.get();
        List<Role> roles = roleRepository.findAllById(Collections.singleton(userRoleDTO.getRoleIds()));
        if (roles.isEmpty()) {
            throw new RuntimeException("No roles found for the provided role IDs");
        }

        // Assign roles to the employee
        user.setRole(roles);

        // Save and return the updated employee
        return userRepository.save(user);
    }


    public void deleteEmployee(long id) {
        userRepository.deleteById(id);
    }

    public String generateVerificationCode(long id) {
        Optional<User> optionalUser= userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerificationCode(generateCode(6));

            userRepository.save(user);

        }
        return null;
    }

    public void setEmployeeVerified(long id, boolean isVerified) {
        Optional<User> employee = userRepository.findById(id);
        if (employee.isPresent()) {
            User emp = employee.get();
            emp.setVerifiedFlag("Y");
            userRepository.save(emp);
        }
    }

    public void setEmployeeLocked(long id, boolean isLocked) {
        Optional<User> employee = userRepository.findById(id);
        if (employee.isPresent()) {
            User emp = employee.get();
            emp.setLockedFlag("Y");
            userRepository.save(emp);
        }
    }

    public void setEmployeeDeleted(long id, boolean isDeleted) {
        Optional<User> employee = userRepository.findById(id);
        if (employee.isPresent()) {
            User emp = employee.get();
            emp.setDeletedFlag("Y");
            userRepository.save(emp);
        }
    }

    public List<User> findAllEmployees() {
        return userRepository.findAll();
    }


    public String generateCode(int length) {
        final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CODE_CHARACTERS.length());
            codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
    public String generateUserName( UserDTO userDTO) {
        String firstName = userDTO.getFirstName();
        String middleName = userDTO.getMiddleName();
        String lastName = userDTO.getLastName();
        if (middleName != null && !middleName.isEmpty()) {
            String initial = middleName.substring(0, 1).toUpperCase() + ". ";
            return firstName + " " + initial + lastName;
        }

        else if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }

        else if (firstName != null && !firstName.isEmpty()) {
            return firstName;
        } else if (lastName != null && !lastName.isEmpty()) {
            return lastName;
        }
        return "Anonymous User";
    }
    public User requestResetVerificationCode(Long id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setResetPasswordVerification(generateCode(6));
                user.setResetVerificationTime(LocalDateTime.now());
                 userRepository.save(user);
                String toEmail = user.getEmail();
                String subject =  "Password reset verification code";
                String text =  "Dear, "+ user.getFirstName() +" your password reset verification code is  "+ user.getResetPasswordVerification();
                emailSender.sendEmailWithVerificationCode(toEmail, subject, text ) ;
                return  user;
            } else {
                throw new EntityNotFoundException("User not found with ID: " + id);
            }
        } catch (Exception e) {
            // Log the exception
            throw new RuntimeException("Error requesting verification code", e);
        }
    }
    public User requestVerificationCode(Long id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setVerificationCode(generateCode(6));
                user.setVerificationTime(LocalDateTime.now());
                userRepository.save(user);
                String toEmail = user.getEmail();
                String subject =  "Password verification code";
                String text =  "Dear, "+ user.getFirstName() +" your verification code is "+ user.getVerificationCode();
                emailSender.sendEmailWithVerificationCode(toEmail, subject, text ) ;
                return  user;
            } else {
                throw new EntityNotFoundException("User not found with ID: " + id);
            }
        } catch (Exception e) {
            // Log the exception
            throw new RuntimeException("Error requesting verification code", e);
        }
    }


}
