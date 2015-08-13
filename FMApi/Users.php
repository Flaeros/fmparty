<?php
require_once 'Consts.php';
require_once 'User.php';

class Users{

    private $link;

    public function Users(){
        $this->link = mysql_connect(Consts::DB_HOST, Consts::DB_USER, Consts::DB_PASS);
        mysql_select_db(Consts::DB_NAME, $this->link);
        mysql_set_charset('utf8',$this->link);
    }


    public function getUserById($id){
        $result = mysql_query(self::$SELECT_BY_ID.$id, $this->link);
        $row = mysql_fetch_row($result);
        dlog($row);
        if($row != false) {
            $user = new User($row[0], $row[1], $row[2], $row[3]);
            return $user;
        }
        return false;
    }
    
    public function getUserBySocNet($socUserId, $socNetId){
        $query = str_replace('{1}', $socUserId, self::$SELECT_BY_SOC_NET_ID);
        $query = str_replace('{2}', $socNetId, $query);
        $result = mysql_query($query, $this->link);
        $row = mysql_fetch_row($result);
        
        if($row != false) {
            dlog("getUserBySocNetId users");
            dlog($row);
            $user = new User($row[0], $row[1], $row[2], $row[3]);
            
            return $user;
        }
        return false;
    }
    
    public function updateUser(User $user){
        $query = str_replace('{1}', $user->getName(), self::$UPDATE_USER_NAME);
        $query = str_replace('{2}', $user->getId(), $query);
        $result = mysql_query($query, $this->link, $this->link);
        return $result;
    }
    
    public function createUser(User $user){
        $query = str_replace('{1}', $user->getSocNetId(), self::$INSERT_USER);
        $query = str_replace('{2}', $user->getSocUserId(), $query);
        $query = str_replace('{3}', $user->getName(), $query);
        dlog($query);
        $result = mysql_query($query, $this->link);
        return $result;
    }
    
    private static $SELECT_BY_ID = 'SELECT * FROM fm_users where id = ';
    private static $SELECT_BY_SOC_NET_ID = 'SELECT * FROM fm_users where socuserid = {1} and socnet = {2}';
    private static $INSERT_USER = "INSERT INTO fm_users(socnet, socuserid, name) values ({1}, {2}, '{3}')";
    private static $UPDATE_USER_NAME = 'UPDATE fm_users set name = {1} where id = {2}';
    
}