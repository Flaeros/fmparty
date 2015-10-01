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
        dlog($result);
        dlog(self::$SELECT_BY_ID.$id);
        $row = mysql_fetch_row($result);
        dlog($row);
        if($row != false) {
            $user = new User($row[0], $row[1], $row[2], $row[3], $row[4]);
            dlog("got");
            dlog($user);
            return $user;
        }
        return false;
    }
    
    public function getUserIdBySocNet($socUserId, $socNetId){
        $query = str_replace('{1}', $socUserId, self::$SELECT_BY_SOC_NET_ID);
        $query = str_replace('{2}', $socNetId, $query);
        dlog($query);
        $result = mysql_query($query, $this->link);
        $row = mysql_fetch_row($result);
        dlog("getUserIdBySocNet");
        dlog($result);
        
        if($row != false) {
            dlog($row);
            return $row[0];
        }
        return false;
    }
    
    public function getUserBySocNet($socUserId, $socNetId){
        $query = str_replace('{1}', $socUserId, self::$SELECT_BY_SOC_NET_ID);
        $query = str_replace('{2}', $socNetId, $query);
        $result = mysql_query($query, $this->link);
        $row = mysql_fetch_row($result);
        
        if($row != false) {
            dlog("getUserBySocNet users");
            dlog($row);
            $user = new User($row[0], $row[1], $row[2], $row[3], $row[4]);
            
            return $user;
        }
        return false;
    }
    
    public function updateUser($userId, $filename, $userName){
        $query = str_replace('{1}', $userName, self::$UPDATE_USER);
        $query = str_replace('{2}', $filename, $query);
        $query = str_replace('{3}', $userId, $query);
        $result = mysql_query($query, $this->link);
        return $result;
    }
    
    public function createUser(User $user){
        $query = str_replace('{1}', $user->socNetId, self::$INSERT_USER);
        $query = str_replace('{2}', $user->socUserId, $query);
        $query = str_replace('{3}', $user->name, $query);
        dlog($query);
        $result = mysql_query($query, $this->link);
        return $result;
    }
    
    private static $SELECT_BY_ID = 'SELECT * FROM fm_users where id = ';
    private static $SELECT_BY_SOC_NET_ID = 'SELECT * FROM fm_users where socuserid = {1} and socnet = {2}';
    private static $INSERT_USER = "INSERT INTO fm_users(socnet, socuserid, name) values ({1}, {2}, '{3}')";
    private static $UPDATE_USER = "UPDATE fm_users set name = '{1}', image = '{2}' where id = {3}";
    
}