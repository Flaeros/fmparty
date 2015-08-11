<?php

class User{
    private $id;
    private $socnet;
    private $socUserId;
    private $name;

    public function User($socnet, $socUserId, $name){
        $this->setId($id);
        $this->setSocnet($socnet);
        $this->setSocUserId($socUserId);
        $this->setName($name);
    }
    
    public function User($id, $socnet, $socUserId, $name){
        $this->setId($id);
        $this->setSocnet($socnet);
        $this->setSocUserId($socUserId);
        $this->setName($name);
    }
    
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}

    public function getSocnet(){ return $this->socnet;}
    public function setSocnet($socnet){ $this->socnet = $socnet;}
    
    public function getSocUserId(){ return $this->socUserId;}
    public function setSocUserId($socUserId){ $this->socUserId = $socUserId;}
    
    public function getName(){ return $this->name;}
    public function setName($name){ $this->name = $name;}
}