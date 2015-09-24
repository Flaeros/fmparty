<?php
require_once 'Chat.php';

class ChatApi {
    
    private $link;

    public function ChatApi(){
        $this->link = mysql_connect(Consts::DB_HOST, Consts::DB_USER, Consts::DB_PASS);
        mysql_select_db(Consts::DB_NAME, $this->link);
        mysql_set_charset('utf8',$this->link);
    }
    
    public function createChat($userId, $chatName, $chatDescr, $chatDate, $chatCity){
        $query = str_replace('{1}', $userId, self::$INSERT_CHAT);
        $query = str_replace('{2}', $chatName, $query);
        $query = str_replace('{3}', $chatDescr, $query);
        $query = str_replace('{4}', $chatDate, $query);
        $query = str_replace('{5}', $chatCity, $query);
        dlog('createChat');
        dlog($query);
        $result = mysql_query($query, $this->link);
        dlog($result);
        if(!$result)
            return false;
        else {
            $chatId = mysql_insert_id($this->link);
            
            $this->insertRef($userId, $chatId);
             
            return $chatId;
        }
    }
    
    public function updateChatImage($chatId, $filename){
        $query = str_replace('{1}', $filename, self::$UPDATE_CHAT_IMAGE);
        $query = str_replace('{2}', $chatId, $query);
        dlog($query);
        $result = mysql_query($query, $this->link);
        dlog($result);
        return $result;
    }
    
    public function insertRef($userId, $chatId){
        $query = str_replace('{1}', $userId, self::$INSERT_REF);
        $query = str_replace('{2}', $chatId, $query);
        $result = mysql_query($query, $this->link);
        
        return $result;
    }
    
    public function removeRef($userId, $chatId){
        $query = str_replace('{1}', $userId, self::$REMOVE_REF);
        $query = str_replace('{2}', $chatId, $query);
        $result = mysql_query($query, $this->link);
        
        return $result;
    }

    public function getChat($chatId){
        $query = str_replace('{1}', $chatId, self::$SELECT_CHAT);
        $result = mysql_query($query, $this->link);
        $row = mysql_fetch_row($result);
        
        dlog('ChatApi getChat');
        dlog($query);
        dlog($row);
        
        if($row != false) {
            $chat = new Chat($row[0], $row[1], $row[2],
                    ($row[3] != NULL ? $row[3] : ''),
                    ($row[4] != NULL ? $row[4] : ''),
                    ($row[5] != NULL ? $row[5] : ''),
                    ($row[6] != NULL ? $row[6] : ''));
            return $chat;
        }
        return false;
    }
    
    public function getChats($socUserId, $socNetId){
        $userApi = new Users();
        $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
        
        $query = str_replace('{1}', $userId, self::$SELECT_USER_CHAT);
        $result = mysql_query($query, $this->link);
        dlog('ChatApi getChats');
        dlog($result);
        dlog($query);
        if(!$result)
            return false;
        
        $chatArray;
        while($row = mysql_fetch_array($result)){
            $chat = new Chat($row['id'], $row['admin_id'], $row['name'], 
                    ($row['image'] != NULL ? $row['image'] : '' ) ,
                    ($row['descr'] != NULL ? $row['descr'] : ''), 
                    ($row['fdate'] != NULL ? $row['fdate'] : ''),
                    ($row['city'] != NULL) ? $row['city'] : '');
            $chatArray[] = $chat;
        }
        
        return $chatArray;
    }
    
    public function findChats($mobName, $mobDescrStr, $mobDateStr, $mobCityStr, $useDate, $userId){
        dlog($mobName);
        dlog($mobDescrStr);
        dlog($mobDateStr);
        dlog($mobCityStr);
        dlog($useDate);
        
        $query = str_replace('{1}', $userId, self::$SELECT_CHATS);
        
        if($mobName != "")
            $query .= "AND name like '%{$mobName}%' ";
        if($mobDescrStr != "")
            $query .= "AND descr like '%{$mobDescrStr}%' ";
        if($mobCityStr != "")
            $query .= "AND city like '%{$mobCityStr}%' ";            
        if($useDate == "true")
            $query .= "AND fdate like '{$mobDateStr}' ";
            
        $query .= 'LIMIT 100';
            
        $result = mysql_query($query, $this->link);
        dlog('ChatApi findChats');
        dlog($result);
        dlog($query);
        if(!$result)
            return false;
        
        $chatArray;
        while($row = mysql_fetch_array($result)){
            $chat = new Chat($row['id'], $row['admin_id'], $row['name'], 
                    ($row['image'] != NULL ? $row['image'] : '' ) ,
                    ($row['descr'] != NULL ? $row['descr'] : ''), 
                    ($row['fdate'] != NULL ? $row['fdate'] : ''),
                    ($row['city'] != NULL) ? $row['city'] : '');
            $chatArray[] = $chat;
        }
        
        return $chatArray;
    }
    
   private static $INSERT_CHAT = "INSERT INTO fm_chats(admin_id, name, descr, fdate, city) values ({1}, '{2}', '{3}', '{4}', '{5}')";
   private static $INSERT_REF = "INSERT INTO fm_refs values ({1}, '{2}')";
   private static $SELECT_CHAT = "SELECT * FROM fm_chats WHERE id = {1}";
   private static $SELECT_USER_CHAT = "SELECT c.* FROM fm_chats c, fm_refs r WHERE r.user_id = {1} AND c.id = r.chat_id";
   private static $SELECT_CHATS = "SELECT * FROM fm_chats WHERE id NOT IN (select chat_id from fm_refs WHERE user_id = {1}) ";
   private static $UPDATE_CHAT_IMAGE = "UPDATE fm_chats set image = '{1}' WHERE id = {2}";
   private static $REMOVE_REF = "DELETE FROM fm_refs where user_id = {1} and chat_id = {2}";
}
