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
        $this->name = $name;
        $this->admin = $admin;
    }
    
    function __construct7($id, $admin, $name, $image, $descr, $date, $city){
        $this->id = $id;
        $this->admin = $admin;
        $this->name = $name;
        $this->image = $image;
        $this->descr = $descr;
        $this->date = $date;
        $this->city = $city;
    }
}
