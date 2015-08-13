<?php

class Consts {
    const DB_HOST = 'localhost';
    const DB_USER = 'host1370739'; 
    const DB_PASS = 'd854c74b';
    const DB_NAME = 'host1370739';
    
    const DB_ERROR = 0;
    const DB_SUCCESS = 1;
    
    const LOG_FILE = 'log.txt';
}

function dlog($str){
    $log = file_get_contents(Consts::LOG_FILE);
    file_put_contents(Consts::LOG_FILE, $log."\n".var_export($str, true));
}