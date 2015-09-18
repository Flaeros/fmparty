<?php

class Chat {
    public $id;
    public $admin;
    public $name;
    public $image;
    public $descr;
    public $date;
    public $city;

    
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
    
    function __construct7($id, $admin, $name, $image, $descr, $date, $city){
        $this->setId($id);
        $this->setAdmin($admin);
        $this->setName($name);
        $this->setImage($image);
        $this->setDescr($descr);
        $this->setDate($date);
        $this->setCity($city);
    }
       
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}

    public function getAdmin(){ return $this->admin;}
    public function setAdmin($admin){ $this->admin = $admin;}
    
    public function getName(){ return $this->name;}
    public function setName($name){ $this->name = $name;}
    
    public function getImage(){ return $this->image;}
    public function setImage($image){ $this->image = $image;}
    
    public function getDescr(){ return $this->descr;}
    public function setDescr($descr){ $this->descr = $descr;}
    
    public function getDate(){ return $this->date;}
    public function setDate($date){ $this->date = $date;}
    
    public function getCity(){ return $this->city;}
    public function setCity($city){ $this->city = $city;}
}
