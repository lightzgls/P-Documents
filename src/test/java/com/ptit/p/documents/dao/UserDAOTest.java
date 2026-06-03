package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;
    private Connection con;

    @BeforeEach
    public void setUp() throws SQLException {
        userDAO = new UserDAO();
        con = userDAO.getCon(); 
        con.setAutoCommit(false); 
    }

    @AfterEach
    public void tearDown() throws SQLException {
        con.rollback(); 
        con.setAutoCommit(true);
    }
    // --- CHỨC NĂNG ĐĂNG NHẬP (checkLogin) ---

    @Test
    public void testCheckLoginStandard() {
        User testUser = new User();
        testUser.setUsername("hoangnd");
        testUser.setPassword("123456");
        testUser.setFullName("Nguyễn Đình Hoàng");
        testUser.setPhone("0911222333");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        User loginRequest = new User();
        loginRequest.setUsername("hoangnd");
        loginRequest.setPassword("123456");

        User result = userDAO.checkLogin(loginRequest);
        assertNotNull(result);
        assertEquals("hoangnd", result.getUsername());
        assertEquals("Nguyễn Đình Hoàng", result.getFullName());
        assertEquals("librarian", result.getRole());
    }

    @Test
    public void testCheckLoginException() {
        User testUser = new User();
        testUser.setUsername("hoangnd_fake");
        testUser.setPassword("123456");
        testUser.setFullName("Tài Khoản Lỗi");
        testUser.setPhone("0911222334");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        User loginRequest = new User();
        loginRequest.setUsername("hoangnd_fake");
        loginRequest.setPassword("wrongpassword");
        
        User result = userDAO.checkLogin(loginRequest);
        assertNull(result);
    }

    // --- CHỨC NĂNG TÌM KIẾM (searchUser) ---

    @Test
    public void testSearchUserStandard() {
        User testUser = new User();
        testUser.setUsername("tuanvq");
        testUser.setPassword("123456");
        testUser.setFullName("Vũ Quang Tuấn");
        testUser.setPhone("0988777666");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        List<User> results = userDAO.searchUser("Quang Tuấn");
        assertFalse(results.isEmpty());
        
        boolean found = false;
        for (User u : results) {
            if (u.getUsername().equals("tuanvq")) {
                assertEquals("Vũ Quang Tuấn", u.getFullName());
                found = true;
                break;
            }
        }
        assertTrue(found, "Phải tìm thấy user vừa tạo ra.");
    }

    @Test
    public void testSearchUserNotFoundException() {
        List<User> results = userDAO.searchUser("nonexistentuser_random_123");
        assertTrue(results.isEmpty());
    }

    // --- CHỨC NĂNG THÊM TÀI KHOẢN (addUser) ---

    @Test
    public void testAddUserStandard() {
        User newUser = new User();
        newUser.setUsername("linhpt");
        newUser.setPassword("password");
        newUser.setFullName("Phạm Thùy Linh");
        newUser.setPhone("0911222333");
        newUser.setRole("librarian");

        boolean addSuccess = userDAO.addUser(newUser);
        assertTrue(addSuccess);

        List<User> searchResults = userDAO.searchUser("linhpt");
        assertFalse(searchResults.isEmpty());
        assertEquals("Phạm Thùy Linh", searchResults.get(0).getFullName());
    }

    @Test
    public void testAddUserException() {
        User firstUser = new User();
        firstUser.setUsername("minhpd"); 
        firstUser.setPassword("123456");
        firstUser.setFullName("Phan Đình Minh");
        firstUser.setPhone("0933444555");
        firstUser.setRole("manager");
        assertTrue(userDAO.addUser(firstUser));

        User duplicateUser = new User();
        duplicateUser.setUsername("minhpd"); 
        duplicateUser.setPassword("123456");
        duplicateUser.setFullName("Phan Đình Minh 2");
        duplicateUser.setPhone("0933444666");
        duplicateUser.setRole("librarian");

        boolean addDuplicateSuccess = userDAO.addUser(duplicateUser);
        assertFalse(addDuplicateSuccess);
    }

    @Test
    public void testAddUser_EmptyOrNullFields() {
        User u = new User();
        
        assertFalse(userDAO.addUser(u), "Tài khoản chứa giá trị Null ở các trường bắt buộc phải bị từ chối");

        u.setUsername("");
        u.setPassword("");
        u.setFullName("");
        u.setPhone("");
        u.setRole("librarian");
        assertFalse(userDAO.addUser(u), "Tài khoản chứa chuỗi rỗng (Empty) phải bị từ chối");
    }

    // --- CHỨC NĂNG CẬP NHẬT TÀI KHOẢN (updateUser) ---

    @Test
    public void testUpdateUserStandard() {
        User testUser = new User();
        testUser.setUsername("quangnd");
        testUser.setPassword("123456");
        testUser.setFullName("Nguyễn Đức Quang");
        testUser.setPhone("0966777888");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        List<User> searchResults = userDAO.searchUser("quangnd");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        userToUpdate.setFullName("Nguyễn Đức Quang S");
        userToUpdate.setPhone("0966777999");

        boolean updateSuccess = userDAO.updateUser(userToUpdate);
        assertTrue(updateSuccess);

        List<User> updatedResults = userDAO.searchUser("quangnd");
        assertFalse(updatedResults.isEmpty());
        assertEquals("Nguyễn Đức Quang S", updatedResults.get(0).getFullName());
        assertEquals("0966777999", updatedResults.get(0).getPhone());
    }

    @Test
    public void testUpdateUserException() {
        User targetUser = new User();
        targetUser.setUsername("tuanma");
        targetUser.setPassword("123456");
        targetUser.setFullName("Mai Anh Tuấn");
        targetUser.setPhone("0977111222");
        targetUser.setRole("librarian");
        userDAO.addUser(targetUser);
        
        User conflictUser = new User();
        conflictUser.setUsername("hungnq");
        conflictUser.setPassword("123456");
        conflictUser.setFullName("Nguyễn Quang Hưng");
        conflictUser.setPhone("0977333444");
        conflictUser.setRole("librarian");
        userDAO.addUser(conflictUser);

        List<User> searchResults = userDAO.searchUser("tuanma");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        userToUpdate.setUsername("hungnq");

        boolean updateDuplicateSuccess = userDAO.updateUser(userToUpdate);
        assertFalse(updateDuplicateSuccess);
    }

    @Test
    public void testUpdateUser_EmptyOrNullFields() {
        User testUser = new User();
        testUser.setUsername("hieund");
        testUser.setPassword("123456");
        testUser.setFullName("Nguyễn Đình Hiếu");
        testUser.setPhone("0988111222");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        List<User> searchResults = userDAO.searchUser("hieund");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        userToUpdate.setUsername("");
        userToUpdate.setPassword("");
        userToUpdate.setPhone("");
        assertFalse(userDAO.updateUser(userToUpdate), "Tài khoản chứa chuỗi rỗng ở các trường bắt buộc phải bị từ chối khi cập nhật");

        userToUpdate.setUsername(null);
        userToUpdate.setPassword(null);
        userToUpdate.setPhone(null);
        assertFalse(userDAO.updateUser(userToUpdate), "Tài khoản chứa giá trị Null ở các trường bắt buộc phải bị từ chối khi cập nhật");
    }

    // --- CHỨC NĂNG XÓA TÀI KHOẢN (deleteUser) ---

    @Test
    public void testDeleteUserStandard() {
        User toDelete = new User();
        toDelete.setUsername("khoihd");
        toDelete.setPassword("123456");
        toDelete.setFullName("Hoàng Đình Khôi");
        toDelete.setPhone("0944555666");
        toDelete.setRole("librarian");
        userDAO.addUser(toDelete);
        
        List<User> searchBefore = userDAO.searchUser("khoihd");
        assertFalse(searchBefore.isEmpty());
        User createdUser = searchBefore.get(0);

        boolean deleteSuccess = userDAO.deleteUser(createdUser);
        assertTrue(deleteSuccess);

        List<User> searchAfter = userDAO.searchUser("khoihd");
        assertTrue(searchAfter.isEmpty());
    }
}
