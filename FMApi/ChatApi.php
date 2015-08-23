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
        dlog($result);
        if(!$result)
            return false;
        else {
            $chatId = mysql_insert_id($this->link);
            
            $query = str_replace('{1}', $userId, self::$INSERT_REF);
            $query = str_replace('{2}', $chatId, $query);
            $result = mysql_query($query, $this->link);
            
            return $chatId;
        }
    }
   
    public function getChats($socUserId, $socNetId){
        $userApi = new Users();
        $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
        
        $query = str_replace('{1}', $userId, self::$SELECT_CHAT);
        $result = mysql_query($query, $this->link);
        
        if(!$result)
            return false;
        
        $chatArray;
        while($row = mysql_fetch_array($result)){
            $chat = new Chat($row['id'], $row['admin_id'], $row['name']);
            $chatArray[] = $chat;
        }
        
        return $chatArray;
    }
    
   private static $INSERT_CHAT = "INSERT INTO fm_chats(admin_id, name) values ({1}, '{2}')";
   private static $INSERT_REF = "INSERT INTO fm_refs values ({1}, '{2}')";
   private static $SELECT_CHAT = "SELECT c.* FROM fm_chats c, fm_refs r WHERE r.user_id = {1} AND c.id = r.chat_id";
}
