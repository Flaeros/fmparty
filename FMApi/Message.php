<?php

class Message {
    public $id;
    public $chatId;
    public $userId;
    public $userName;
    public $text;
    
    function __construct(){
        $a = func_get_args(); 
        $i = func_num_args(); 
        if (method_exists($this,$f='__construct'.$i)) { 
            call_user_func_array(array($this,$f),$a); 
        } 
    }

    function __construct3($chatId, $userId, $text){
        $this->setChatId($chatId);
        $this->setUserId($userId);
        $this->setText($text);
    }
    
    function __construct5($id, $chatId, $userId, $userName, $text){
        $this->setId($id);
        $this->setChatId($chatId);
        $this->setUserId($userId);
        $this->setUserName($userName);
        $this->setText($text);
    }
    
    public function getId(){ return $this->id;}
    public function setId($id){ $this->id = $id;}
    
    public function getChatId(){ return $this->chatId;}
    public function setChatId($chatId){ $this->chatId = $chatId;}
    
    public function getUserId(){ return $this->userId;}
    public function setUserId($userId){ $this->userId = $userId;}
    
    public function getUserName(){ return $this->userName;}
    public function setUserName($userName){ $this->userName = $userName;}
    
    public function getText(){ return $this->text;}
    public function setText($text){ $this->text = $text;}
}
