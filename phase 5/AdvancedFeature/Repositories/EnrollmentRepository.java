/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdvancedFeature.Repositories;

import Mutlithread.Enrollments;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author ahlamabudiab
 */
public class EnrollmentRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdvancedFeature");
    public List<Enrollments> findByStudentAndRange(int studentId, LocalDate from, LocalDate to){
        EntityManager em  = emf.createEntityManager();
        try{
            List<Enrollments> list;
            if(studentId == 0){
                list= em.createQuery("SELECT e FROM Enrollments e WHERE e.enrollmentDate BETWEEN :f AND :t",Enrollments.class)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .getResultList(); 
            }else{
                list =em.createQuery("SELECT e FROM Enrollments e WHERE e.student.id= :sid AND e.enrollmentDate BETWEEN :f AND :t")
                        .setParameter("sid", studentId)
                        .setParameter("f", from)
                        .setParameter("t", to)
                        .getResultList();
            }
            return (list == null) ? java.util.Collections.emptyList() : list;
        }finally{
            em.close();
        }
    }
    public Long countByCourse(Integer courseId){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT COUNT(e) FROM Enrollments e WHERE e.course.id =:cid",Long.class)
                   .setParameter("cid", courseId)
                   .getSingleResult();
        }finally{
            em.close();
        }
    }
    public Long countEnrollment(){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT COUNT(e) FROM Enrollments e",Long.class)
                   .getSingleResult();
        }finally{
            em.close();
        }
    }
    public boolean existsByStudentAndCourse(Integer studentId, Integer courseId){
        EntityManager em  = emf.createEntityManager();
        try{
            Long count = em.createQuery("SELECT COUNT(e) FROM Enrollments e WHERE e.student.id =:sid AND e.course.id =:cid", Long.class)
                    .setParameter("sid", studentId)
                    .setParameter("cid", courseId)
                    .getSingleResult();
            return count!=null && count>0;
        }finally{
            em.close();
        }
    }
    public List<Enrollments> search(String search, LocalDate date){
        EntityManager em  = emf.createEntityManager();
        try{
            boolean hasText = search != null && !search.isBlank();
            if(date != null && !hasText){
                return em.createQuery("SELECT e FROM Enrollments e JOIN FETCH e.student s JOIN FETCH e.course c WHERE e.enrollmentDate = :date")
                        .setParameter("date", date)
                        .getResultList();
            }
            if(search != null && !search.isBlank()){
                String text = "%" +search.toLowerCase()+"%";
                return em.createQuery("SELECT e FROM Enrollments e JOIN e.student s JOIN e.course c " +
              "WHERE LOWER(s.name) LIKE :search OR LOWER(c.title) LIKE :search " +
              " OR LOWER(c.department) LIKE :search ORDER BY e.id")
                        .setParameter("search", text)
                        .getResultList();
            }
            return Collections.emptyList();
        }finally{
            em.close();
        }
    }
     
    public Enrollments save(Enrollments enroll){
        EntityManager em  = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            if(enroll.getId() == null){
                em.persist(enroll);
            }else{
                em.merge(enroll);
            }
            em.getTransaction().commit();
            return enroll;
        }finally{
            em.close();
        }
    }
    public void delete(Integer id){
        EntityManager em  = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Enrollments delEnroll = em.find(Enrollments.class, id);
            if(delEnroll != null){
               em.remove(delEnroll);
            }
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }
}
