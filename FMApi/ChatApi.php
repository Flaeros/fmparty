<?php
require_once 'Chat.php';

class ChatApi {
    
    private $link;

    public function ChatApi(){
        $this->link = mysql_connect(Consts::DB_HOST, Consts::DB_USER, Consts::DB_PASS);
        mysql_select_db(Consts::DB_NAME, $this->link);
        mysql_set_charset('utf8',$this->link);
    }
    
    public function createChat($userId, $chatName){
        $query = str_replace('{1}', $userId, self::$INSERT_CHAT);
        $query = str_replace('{2}', $chatName, $query);
        dlog($query);
        $result = mysql_query($query, $this->link);
        
        if(!$result)
            return false;
        else
            return mysql_insert_id($this->link);
    }
   
   private static $INSERT_CHAT = "INSERT INTO fm_chats(admin_id, name) values ({1}, '{2}')";
}
