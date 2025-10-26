/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdvancedFeature.Repositories;

import Mutlithread.Courses;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author ahlamabudiab
 */
public class CourseRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdvancedFeature");
    public List<Courses> findAll(){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT c FROM Courses c",Courses.class).getResultList();
        }finally{
            em.close();
        }
    }
    public List<Courses> fibdByDepartment(String dept){
        EntityManager em  = emf.createEntityManager();
        try{
           return em.createQuery("SELECT c FROM Courses c WHERE c.department =:dept",Courses.class)
                   .setParameter("dept", dept)
                   .getResultList();
        }finally{
           em.close();
        }
        
    }
    public Long countCourses(){
        EntityManager em  = emf.createEntityManager();
        try{
            return em.createQuery("SELECT COUNT(c) FROM Courses c ",Long.class)
                   .getSingleResult();
        }finally{
            em.close();
        }
    }
    public Courses save(Courses course){
        EntityManager em  = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            if(course.getId()==null){
                em.persist(course);
            }else{
                em.merge(course);
            }
            em.getTransaction().commit();
            return course;
        }finally{
            em.close();
        }
        
    }
    public boolean dupCourses(String title, String department, int credits){
        EntityManager em  = emf.createEntityManager();
        Long dup = (Long) em.createQuery("SELECT COUNT(c) FROM Courses c WHERE LOWER(c.title) =LOWER(:title) AND LOWER(c.department)= LOWER(:department) AND c.credits = :cr")
                .setParameter("title", title)
                .setParameter("department", department)
                .setParameter("cr", credits)
                .getSingleResult();
        em.close();
        return dup!=null && dup>0;
    }
    public List<Courses> search(String keyword) {
        EntityManager em = emf.createEntityManager();
        try {
            String search = "%" + keyword.toLowerCase() + "%";
            Integer cr = null;
            try{
            cr = Integer.valueOf(keyword.trim());
            } catch (NumberFormatException ignore) {
            }
            String sql = "SELECT c FROM Courses c WHERE LOWER (c.title) LIKE :search OR LOWER(c.department) LIKE :search";
            if(cr!=null)
                sql += " OR c.credits = :cr";
            TypedQuery<Courses> searchCourses = em.createQuery(sql, Courses.class)
                    .setParameter("search", "%" + search.toLowerCase() + "%");
            if (cr != null) 
                searchCourses.setParameter("cr", cr);
           return searchCourses.getResultList();
        } finally { 
            em.close();
        }
    }
    public void delete(Integer id){
        EntityManager em  = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Courses delCourse = em.find(Courses.class, id);
            if(delCourse != null){
                em.remove(delCourse);
            }
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }
}
