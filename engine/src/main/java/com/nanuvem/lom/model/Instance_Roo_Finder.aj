// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.model;

import com.nanuvem.lom.model.Entity;
import com.nanuvem.lom.model.Instance;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Instance_Roo_Finder {
    
    public static TypedQuery<Instance> Instance.findInstancesByEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("The entity argument is required");
        EntityManager em = Instance.entityManager();
        TypedQuery<Instance> q = em.createQuery("SELECT o FROM Instance AS o WHERE o.entity = :entity", Instance.class);
        q.setParameter("entity", entity);
        return q;
    }
    
}