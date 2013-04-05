// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.web;

import com.nanuvem.lom.model.Entity;
import com.nanuvem.lom.service.EntityService;
import com.nanuvem.lom.web.EntityController;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect EntityController_Roo_Controller_Json {
    
    @Autowired
    EntityService EntityController.entityService;
    
    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.showJson(@PathVariable("id") Long id) {
        Entity entity = entityService.findEntity(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (entity == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(entity.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Entity> result = entityService.findAllEntitys();
        return new ResponseEntity<String>(Entity.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> EntityController.createFromJson(@RequestBody String json) {
        Entity entity = Entity.fromJsonToEntity(json);
        entityService.saveEntity(entity);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> EntityController.createFromJsonArray(@RequestBody String json) {
        for (Entity entity: Entity.fromJsonArrayToEntitys(json)) {
            entityService.saveEntity(entity);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> EntityController.updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Entity entity = Entity.fromJsonToEntity(json);
        if (entityService.updateEntity(entity) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> EntityController.updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Entity entity: Entity.fromJsonArrayToEntitys(json)) {
            if (entityService.updateEntity(entity) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> EntityController.deleteFromJson(@PathVariable("id") Long id) {
        Entity entity = entityService.findEntity(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (entity == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        entityService.deleteEntity(entity);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByNameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.jsonFindEntitysByNameEquals(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Entity.toJsonArray(Entity.findEntitysByNameEquals(name).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByNameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.jsonFindEntitysByNameLike(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Entity.toJsonArray(Entity.findEntitysByNameLike(name).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByNamespaceEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.jsonFindEntitysByNamespaceEquals(@RequestParam("namespace") String namespace) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Entity.toJsonArray(Entity.findEntitysByNamespaceEquals(namespace).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByNamespaceLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EntityController.jsonFindEntitysByNamespaceLike(@RequestParam("namespace") String namespace) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Entity.toJsonArray(Entity.findEntitysByNamespaceLike(namespace).getResultList()), headers, HttpStatus.OK);
    }
    
}
