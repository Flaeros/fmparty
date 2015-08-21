<?php

class Message {
    public $id;
    public $chat_id;
    public $user_id;
    public $text;
    
    function __construct(){
        $a = func_get_args(); 
        $i = func_num_args(); 
        if (method_exists($this,$f='__construct'.$i)) { 
            call_user_func_array(array($this,$f),$a); 
        } 
    }

    function __construct3($chat_id, $user_id, $text){
        $this->setChatId($chat_id);
        $this->setUserId($user_id);
        $this->setText($text);
    }
    
    function __construct4($id, $chat_id, $user_id, $text){
        $this->setId($id);
        $this->setChatId($chat_id);
        $this->setUserId($user_id);
        $this->setText($text);
    }
    
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}
    
    public function getChatId(){ return $this->chat_id;}
    public function setChatId($chat_id){ $this->chat_id = $chat_id;}
    
    public function getUserId(){ return $this->user_id;}
    public function setUserId($user_id){ $this->user_id = $user_id;}
    
    public function getText(){ return $this->text;}
    public function setText($text){ $this->text = $text;}
}
