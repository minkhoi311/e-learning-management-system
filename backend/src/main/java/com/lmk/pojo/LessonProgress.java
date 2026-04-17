/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "lesson_progress")
@NamedQueries({
    @NamedQuery(name = "LessonProgress.findAll", query = "SELECT l FROM LessonProgress l"),
    @NamedQuery(name = "LessonProgress.findById", query = "SELECT l FROM LessonProgress l WHERE l.id = :id"),
    @NamedQuery(name = "LessonProgress.findByIsCompleted", query = "SELECT l FROM LessonProgress l WHERE l.isCompleted = :isCompleted"),
    @NamedQuery(name = "LessonProgress.findByCompletedTime", query = "SELECT l FROM LessonProgress l WHERE l.completedTime = :completedTime")})
public class LessonProgress implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "is_completed")
    private Boolean isCompleted;
    @Column(name = "completed_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedTime;
    @JoinColumn(name = "enrollment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Enrollment enrollmentId;
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Lesson lessonId;

    public LessonProgress() {
    }

    public LessonProgress(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Enrollment getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Enrollment enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Lesson getLessonId() {
        return lessonId;
    }

    public void setLessonId(Lesson lessonId) {
        this.lessonId = lessonId;
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
        if (!(object instanceof LessonProgress)) {
            return false;
        }
        LessonProgress other = (LessonProgress) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lmk.pojo.LessonProgress[ id=" + id + " ]";
    }
    
}
