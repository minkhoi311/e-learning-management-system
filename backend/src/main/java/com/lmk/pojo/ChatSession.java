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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "chat_session")
@NamedQueries({
    @NamedQuery(name = "ChatSession.findAll", query = "SELECT c FROM ChatSession c"),
    @NamedQuery(name = "ChatSession.findById", query = "SELECT c FROM ChatSession c WHERE c.id = :id"),
    @NamedQuery(name = "ChatSession.findByFirebaseRoom", query = "SELECT c FROM ChatSession c WHERE c.firebaseRoom = :firebaseRoom")})
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "firebase_room")
    private String firebaseRoom;
    @JoinColumn(name = "instructor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User instructorId;
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User studentId;

    public ChatSession() {
    }

    public ChatSession(Integer id) {
        this.id = id;
    }

    public ChatSession(Integer id, String firebaseRoom) {
        this.id = id;
        this.firebaseRoom = firebaseRoom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirebaseRoom() {
        return firebaseRoom;
    }

    public void setFirebaseRoom(String firebaseRoom) {
        this.firebaseRoom = firebaseRoom;
    }

    public User getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(User instructorId) {
        this.instructorId = instructorId;
    }

    public User getStudentId() {
        return studentId;
    }

    public void setStudentId(User studentId) {
        this.studentId = studentId;
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
        if (!(object instanceof ChatSession)) {
            return false;
        }
        ChatSession other = (ChatSession) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.lmk.pojo.ChatSession[ id=" + id + " ]";
    }
    
}
