// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.dao.typesquare;

import com.nanuvem.lom.dao.typesquare.Property;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect Property_Roo_Jpa_Entity {
    
    declare @type: Property: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Property.id;
    
    @Version
    @Column(name = "version")
    private Integer Property.version;
    
    public Long Property.getId() {
        return this.id;
    }
    
    public void Property.setId(Long id) {
        this.id = id;
    }
    
    public Integer Property.getVersion() {
        return this.version;
    }
    
    public void Property.setVersion(Integer version) {
        this.version = version;
    }
    
}
