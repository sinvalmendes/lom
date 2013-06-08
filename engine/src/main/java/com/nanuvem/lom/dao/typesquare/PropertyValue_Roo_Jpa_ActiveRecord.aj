// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.dao.typesquare;

import com.nanuvem.lom.dao.typesquare.PropertyValue;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PropertyValue_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager PropertyValue.entityManager;
    
    public static final EntityManager PropertyValue.entityManager() {
        EntityManager em = new PropertyValue().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long PropertyValue.countPropertyValues() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PropertyValue o", Long.class).getSingleResult();
    }
    
    public static List<PropertyValue> PropertyValue.findAllPropertyValues() {
        return entityManager().createQuery("SELECT o FROM PropertyValue o", PropertyValue.class).getResultList();
    }
    
    public static List<PropertyValue> PropertyValue.findPropertyValueEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PropertyValue o", PropertyValue.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void PropertyValue.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void PropertyValue.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PropertyValue attached = PropertyValue.findPropertyValue(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void PropertyValue.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void PropertyValue.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public PropertyValue PropertyValue.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PropertyValue merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
