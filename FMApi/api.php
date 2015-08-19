<?php
include 'Users.php';
include 'ChatApi.php';
include 'ResultObject.php';

mb_internal_encoding("UTF-8");

$do = $_POST['do'];
switch ($do) {
    case 'createOrGetUser':
        createOrGetUser();
        break;
    case 'createChat':
        createChat();
        break;
    case 'getChats':
        getChats();
        break;
    case 'updateUser':
        updateUser();
        break;
}

function getChats(){
    $chatApi = new ChatApi();
    $socUserId = intval($_POST['socUserId']);
    $socNetId = intval($_POST['socNetId']);
    
    $jsonResult = new ResultObject();
    dlog('getChats');
    
    $result = $chatApi->getChats($socUserId, $socNetId);
    dlog($result);
    
    $jsonResult = new ResultObject();
    if(!$result){
        dlog('false');
        $jsonResult->resultCode = Consts::DB_ERROR;
        $jsonResult->resultObject = "";  
    }
    else{
        dlog('true');
        $jsonResult->resultCode = Consts::DB_SUCCESS;
        $jsonResult->resultObject = $result; 
    }

    dlog($jsonResult);
    echo json_encode($jsonResult);
}

function createChat(){
    $chatApi = new ChatApi();
    $socUserId = intval($_POST['socUserId']);
    $socNetId = intval($_POST['socNetId']);
    $chatName = mysql_real_escape_string($_POST['chatName']);
    
    
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    
    $jsonResult = new ResultObject();
    dlog('createChat');
    
    $result = $chatApi->createChat($userId, $chatName);
    dlog($result);
    if($result > 0){
        dlog('true');
        $result = new Chat($result, $userId, $chatName);
        $jsonResult->resultCode = Consts::DB_SUCCESS;
        $jsonResult->resultObject = $result;   
    }
    else{
        dlog('false');
        $jsonResult->resultCode = Consts::DB_ERROR;
        $jsonResult->resultObject = "";
    }

    dlog($jsonResult);
    echo json_encode($jsonResult);
}

function createOrGetUser(){
    $userApi = new Users();
    
    $socNetId = intval($_POST['socnetid']);
    $socUserId = intval($_POST['socuserid']);
    $name = mysql_real_escape_string($_POST['name']);
    $jsonResult = new ResultObject();
    
    $result = isUserExists($socUserId, $socNetId);
    dlog('createOrGetUser');
    dlog($result);
    if($result){
        dlog('true1');
        $jsonResult->resultCode = Consts::DB_SUCCESS;
        $jsonResult->resultObject = $result;
    }
    else{
        dlog('false1');
        $user = new User($socNetId, $socUserId, $name);
        $result = $userApi->createUser($user);
    
        if($result){
            dlog('true2');
            $result = $userApi->getUserBySocNet($socUserId, $socNetId);
            
            $jsonResult->resultCode = Consts::DB_SUCCESS;
            $jsonResult->resultObject = $result;   
        }
        else{
            dlog('false2');
            $jsonResult->resultCode = Consts::DB_ERROR;
            $jsonResult->resultObject = "";
        }
    }
    dlog($jsonResult);
    echo json_encode($jsonResult);
}

function isUserExists($socUserId, $socNetId){
    $userApi = new Users();
    $result = $userApi->getUserBySocNet($socUserId, $socNetId);
    if(!$result)
        return false;
    else 
        return $result;
}

function updateUser(){
    
}