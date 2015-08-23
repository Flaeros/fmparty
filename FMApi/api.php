<?php
include 'Users.php';
include 'ChatApi.php';
include 'ResultObject.php';
include 'MsgApi.php';

mb_internal_encoding("UTF-8");

$do = $_POST['do'];
switch ($do) {
    case 'createUser':
        createUser();
        break;
    case 'createChat':
        createChat();
        break;
    case 'getChats':
        getChats();
        break;
    case 'sendMsg':
        sendMsg();
        break;
    case 'getMessages':
        getMessages();
        break;
    case 'updateUser':
        updateUser();
        break;
}

function getMessages(){
    $chatId = intval($_POST['chatid']);
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    
    $msgApi = new MsgApi();
    $result = $msgApi->getMessages($chatId);
    dlog('getMessages');
    dlog($result);
    
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    
    $jsonResult = new ResultObject();
    if($result > 0){
        dlog('true');
        $jsonResult->resultCode = Consts::DB_SUCCESS;
        
        $obj = new stdClass;
        $obj->msgs = $result;
        $obj->id = $userId;
        
        $result = $obj;
        
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

function sendMsg(){
    $chatId = intval($_POST['chatid']);
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    
    $text = mysql_real_escape_string($_POST['textMsg']);
    
    $message = new Message($chatId, $userId, $text);
    
    $msgApi = new MsgApi();
    $result = $msgApi->createMsg($message);
    dlog("sendMsg");
    dlog($result);
    
    $jsonResult = new ResultObject();
    if(!$result){
        dlog('false');
        $jsonResult->resultCode = Consts::DB_ERROR;
        $jsonResult->resultObject = "";  
    }
    else{
        dlog('true');
        
        $obj = new stdClass;
        $obj->id = $result;
        $result = $obj;
        
        $jsonResult->resultCode = Consts::DB_SUCCESS;
        $jsonResult->resultObject = $result; 
    }

    dlog($jsonResult);
    echo json_encode($jsonResult);
}

function getChats(){
    $chatApi = new ChatApi();
    $socUserId = intval($_POST['socUserId']);
    $socNetId = intval($_POST['socNetId']);
    
    $jsonResult = new ResultObject();
    dlog('getChats');
    
    $result = $chatApi->getChats($socUserId, $socNetId);
    
    $jsonResult = new ResultObject();
    if($result > 0){
        dlog('true');
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

function createChat(){
    $chatApi = new ChatApi();
    $socUserId = intval($_POST['socUserId']);
    $socNetId = intval($_POST['socNetId']);
    $chatName = mysql_real_escape_string($_POST['chatName']);
    
    dlog($socUserId);
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    dlog($userId);
    
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

function createUser(){
    $userApi = new Users();
    
    $socNetId = intval($_POST['socnetid']);
    $socUserId = intval($_POST['socuserid']);
    $name = mysql_real_escape_string($_POST['name']);
    $jsonResult = new ResultObject();
    
    $result = isUserExists($socUserId, $socNetId);
    dlog('createUser');
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