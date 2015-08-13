<?php
require_once 'Consts.php';

class User {
    public $id;
    public $socNetId;
    public $socUserId;
    public $name;

    function __construct(){
        $a = func_get_args(); 
        $i = func_num_args(); 
        if (method_exists($this,$f='__construct'.$i)) { 
            call_user_func_array(array($this,$f),$a); 
        } 
    }
            
    function __construct3($socNetId, $socUserId, $name){
        $this->setSocNetId($socNetId);
        $this->setSocUserId($socUserId);
        $this->setName($name);
    }
    
    function __construct4($id, $socNetId, $socUserId, $name){
        $this->setId($id);
        $this->setSocNetId($socNetId);
        $this->setSocUserId($socUserId);
        $this->setName($name);
    }
    
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}

    public function getSocNetId(){ return $this->socNetId;}
    public function setSocNetId($socnet){ $this->socNetId = $socnet;}
    
    public function getSocUserId(){ return $this->socUserId;}
    public function setSocUserId($socUserId){ $this->socUserId = $socUserId;}
    
    public function getName(){ return $this->name;}
    public function setName($name){ $this->name = $name;}
}