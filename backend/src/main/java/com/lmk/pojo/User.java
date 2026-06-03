package com.lmk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUsername",
    query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "first_name")
    private String firstName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;

    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Size(max = 500)
    @Column(name = "avatar")
    private String avatar;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "role")
    private String role;

    @Size(max = 6)
    @Column(name = "auth_provider")
    private String authProvider;

    @Column(name = "is_instructor")
    private Boolean isInstructor;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    // ==========================================
    // CÁC MỐI QUAN HỆ (ĐÃ THÊM JSONIGNORE ĐẦY ĐỦ)
    // ==========================================

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructorId")
    @JsonIgnore
    private Set<Course> courseSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    @JsonIgnore
    private Set<LessonComment> lessonCommentSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    @JsonIgnore
    private Set<Enrollment> enrollmentSet;

    // 🔥 FIX LỖI Ở ĐÂY: Đổi tên biến và chặn đệ quy Json cho ChatSession
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructorId")
    @JsonIgnore 
    private Set<ChatSession> instructorChatSessions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    @JsonIgnore 
    private Set<ChatSession> studentChatSessions;

    @Transient
    private MultipartFile file;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    // ==========================================
    // DUY NHẤT 1 BỘ GETTER & SETTER (ĐÃ XÓA RÁC TRÙNG LẶP)
    // ==========================================
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }

    public Boolean getIsInstructor() { return isInstructor; }
    public void setIsInstructor(Boolean isInstructor) { this.isInstructor = isInstructor; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public Date getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(Date updatedTime) { this.updatedTime = updatedTime; }

    public Set<Course> getCourseSet() { return courseSet; }
    public void setCourseSet(Set<Course> courseSet) { this.courseSet = courseSet; }

    public Set<LessonComment> getLessonCommentSet() { return lessonCommentSet; }
    public void setLessonCommentSet(Set<LessonComment> lessonCommentSet) { this.lessonCommentSet = lessonCommentSet; }

    public Set<Enrollment> getEnrollmentSet() { return enrollmentSet; }
    public void setEnrollmentSet(Set<Enrollment> enrollmentSet) { this.enrollmentSet = enrollmentSet; }

    public Set<ChatSession> getInstructorChatSessions() { return instructorChatSessions; }
    public void setInstructorChatSessions(Set<ChatSession> instructorChatSessions) { this.instructorChatSessions = instructorChatSessions; }

    public Set<ChatSession> getStudentChatSessions() { return studentChatSessions; }
    public void setStudentChatSessions(Set<ChatSession> studentChatSessions) { this.studentChatSessions = studentChatSessions; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lmk.pojo.User[ id=" + id + " ]";
    }
}