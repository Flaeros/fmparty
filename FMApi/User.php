<?php
require_once 'Consts.php';

class User {
    public $id;
    public $socNetId;
    public $socUserId;
    public $name;
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
    
    function __construct5($id, $socNetId, $socUserId, $name, $image){
        $this->id = $id;
        $this->socNetId = $socNetId;
        $this->socUserId = $socUserId;
        $this->name = $name;
        $this->image = $image;
    }
}