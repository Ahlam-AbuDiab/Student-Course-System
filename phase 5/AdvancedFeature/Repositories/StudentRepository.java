/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdvancedFeature.Repositories;

import Mutlithread.Students;
import java.util.List;
import javafx.scene.control.Alert;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author ahlamabudiab
 */
public class StudentRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdvancedFeature");
    public List<Students> findAll(){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT s FROM Students s").getResultList();
        }finally{
            em.close();
        }
    }
    public Long countStudents(){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT COUNT(s) FROM Students s ",Long.class)
                   .getSingleResult();
        }finally{
            em.close();
        }
    }
    public boolean dupStd(String name, String major){
        EntityManager em  = emf.createEntityManager();
        Long dup = (Long) em.createQuery("SELECT COUNT(s) FROM Students s WHERE LOWER(s.name) =LOWER(:name) AND LOWER(s.major)= LOWER(:major)")
                .setParameter("name", name)
                .setParameter("major", major)
                .getSingleResult();
        em.close();
        return dup!=null && dup>0;
    }
    public Students save(Students student){
        EntityManager em  = emf.createEntityManager();
        try{
        em.getTransaction().begin();
        if(student.getId()==null){
            em.persist(student);
        }else{
            student = em.merge(student);
        }
        em.getTransaction().commit();
        return student;
        }finally{
        em.close();
        }
    }
    public List<Students> search(String search) {
        EntityManager em = emf.createEntityManager();
        try {
            String searchWord = "%" + search.toLowerCase() + "%";
            return em.createQuery("SELECT s FROM Students s WHERE LOWER (s.name) LIKE :searchWord OR LOWER(s.major) LIKE :searchWord", Students.class)
                     .setParameter("searchWord", searchWord)
                     .getResultList();
        } finally { 
            em.close();
        }
    }
    public void delete(Integer id){
        EntityManager em  = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Students delStudent = em.find(Students.class, id);
            if(delStudent != null){
                em.remove(delStudent);
            }
            em.getTransaction().commit();
        }finally{
          em.close();
        }
    }
}
