<?php
include 'Consts.php';
include 'User.php';

class Users{

    private $link;

    public function Users(){
        $this->link = mysql_connect(Consts::DB_HOST, Consts::DB_USER, Consts::DB_PASS);
        mysql_select_db(Consts::DB_NAME, $this->link);
    }


    public function getUserById($id){
        $result = mysql_query(self::$SELECT_BY_ID.$id);
        $row = mysql_fetch_row($result);
        if($row != false) {
            $user = new User($row[0], $row[1], $row[2], $row[3]);
            return $user;
        }
        return false;
    }
    
    public function updateUser(User $user){
        $query = replace('{1}', $user->getName(), self::$UPDATE_USER_NAME);
        $query = replace('{2}', $user->getId(), $query);
        $result = mysql_query($query);
    }
    
    public function createUser(User $user){
        $query = replace('{1}', $user->getSocnet(), self::$INSERT_USER);
        $query = replace('{2}', $user->getSocUserId(), $query);
        $query = replace('{3}', $user->getName(), $query);
        $result = mysql_query($query);
        return $result;
    }
    
    private static $SELECT_BY_ID = 'SELECT * FROM fm_users where id = ';
    private static $INSERT_USER = 'INSERT INTO fm_users(socnet, socUserId, name) values ({1}, {2}, {3})';
    private static $UPDATE_USER_NAME = 'UPDATE fm_users set name = {1} where id = {2}';
    
}