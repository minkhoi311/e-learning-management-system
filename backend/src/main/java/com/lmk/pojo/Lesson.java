/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "lesson")
@NamedQueries({
    @NamedQuery(name = "Lesson.findAll", query = "SELECT l FROM Lesson l"),
    @NamedQuery(name = "Lesson.findById", query = "SELECT l FROM Lesson l WHERE l.id = :id"),
    @NamedQuery(name = "Lesson.findBySubject", query = "SELECT l FROM Lesson l WHERE l.subject = :subject"),
    @NamedQuery(name = "Lesson.findByImage", query = "SELECT l FROM Lesson l WHERE l.image = :image"),
    @NamedQuery(name = "Lesson.findByIsActive", query = "SELECT l FROM Lesson l WHERE l.isActive = :isActive"),
    @NamedQuery(name = "Lesson.findByCreatedTime", query = "SELECT l FROM Lesson l WHERE l.createdTime = :createdTime"),
    @NamedQuery(name = "Lesson.findByUpdatedTime", query = "SELECT l FROM Lesson l WHERE l.updatedTime = :updatedTime")})
public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "subject")
    private String subject;
    @Lob
    @Size(max = 65535)
    @Column(name = "content")
    private String content;
    @Size(max = 500)
    @Column(name = "image")
    private String image;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Course courseId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lessonId")
    private Set<LessonProgress> lessonProgressSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lessonId")
    private Set<LessonComment> lessonCommentSet;

    public Lesson() {
    }

    public Lesson(Integer id) {
        this.id = id;
    }

    public Lesson(Integer id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Course getCourseId() {
        return courseId;
    }

    public void setCourseId(Course courseId) {
        this.courseId = courseId;
    }

    public Set<LessonProgress> getLessonProgressSet() {
        return lessonProgressSet;
    }

    public void setLessonProgressSet(Set<LessonProgress> lessonProgressSet) {
        this.lessonProgressSet = lessonProgressSet;
    }

    public Set<LessonComment> getLessonCommentSet() {
        return lessonCommentSet;
    }

    public void setLessonCommentSet(Set<LessonComment> lessonCommentSet) {
        this.lessonCommentSet = lessonCommentSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lesson)) {
            return false;
        }
        Lesson other = (Lesson) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lmk.pojo.Lesson[ id=" + id + " ]";
    }
    
}
