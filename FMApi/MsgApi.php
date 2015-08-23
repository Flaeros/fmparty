<?php

require_once 'Message.php';
require_once 'Consts.php';

class MsgApi {
    private $link;

    public function MsgApi(){
        $this->link = mysql_connect(Consts::DB_HOST, Consts::DB_USER, Consts::DB_PASS);
        mysql_select_db(Consts::DB_NAME, $this->link);
        mysql_set_charset('utf8',$this->link);
    }
    
    public function createMsg(Message $message){
        dlog("inside message");
        dlog($message);
        $query = str_replace('{1}', $message->getChatId(), self::$INSERT_MSG);
        $query = str_replace('{2}', $message->getUserId(), $query);
        $query = str_replace('{3}', $message->getText(), $query);
        $result = mysql_query($query, $this->link);
        dlog("createMsg");
        dlog($result);
        if(!$result)
            return false;
        else {
            $msgId = mysql_insert_id($this->link);
            
            return $msgId;
        }
    }
    
    public function getMessages($chatId){
        $query = str_replace('{1}', $chatId, self::$SELECT_MSGS);
        $result = mysql_query($query, $this->link);
        
        if(!$result)
            return false;
        
        $msgsArray;
        while($row = mysql_fetch_array($result)){
            $msg = new Message($row['id'], $row['chat_id'], $row['user_id'], $row['text']);
            $msgsArray[] = $msg;
        }
        
        return $msgsArray;
    }
    
    private static $INSERT_MSG = "INSERT INTO fm_msgs(chat_id, user_id, text) values ({1}, {2}, '{3}')";
    private static $SELECT_MSGS = "SELECT * from fm_msgs where chat_id = {1}";
}
