<?php
require_once 'Consts.php';

class User {
    public $id;
    public $socNetId;
    public $socUserId;
    public $name;
    public $desc;
    public $image;
    

    function __construct(){
        $a = func_get_args(); 
        $i = func_num_args(); 
        if (method_exists($this,$f='__construct'.$i)) { 
            call_user_func_array(array($this,$f),$a); 
        } 
    }
            
    function __construct3($socNetId, $socUserId, $name){
        $this->socNetId = $socNetId;
        $this->socUserId = $socUserId;
        $this->name = $name;
    }
    
    function __construct6($id, $socNetId, $socUserId, $name, $desc, $image){
        $this->id = $id;
        $this->socNetId = $socNetId;
        $this->socUserId = $socUserId;
        $this->name = $name;
        $this->desc = $desc;
        $this->image = $image;
    }
}