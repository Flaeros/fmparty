<?php

class Chat {
    public $id;
    public $admin;
    public $name;

    
    function __construct(){
        $a = func_get_args(); 
        $i = func_num_args(); 
        if (method_exists($this,$f='__construct'.$i)) { 
            call_user_func_array(array($this,$f),$a); 
        } 
    }

    function __construct2($name, $admin){
        $this->setName($name);
        $this->setAdmin($admin);
    }
    
    function __construct3($id, $admin, $name){
        $this->setId($id);
        $this->setAdmin($admin);
        $this->setName($name);
    }
       
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}

    public function getAdmin(){ return $this->admin;}
    public function setAdmin($admin){ $this->admin = $admin;}
    
    public function getName(){ return $this->name;}
    public function setName($name){ $this->name = $name;}
}
