<?php
include 'Users.php';
include 'ChatApi.php';
include 'ResultObject.php';
include 'MsgApi.php';

mb_internal_encoding("UTF-8");

$do = $_POST['do'];

if(!authorize($do)){
    dlog('not authorized');
    return;
}
else{
    dlog('yes authorized');
}

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
    case 'findMobs':
        findMobs();
        break;
    case 'joinMob':
        joinMob();
        break;
    case 'updateChat':
        updateChat();
        break;
    case 'getChat':
        getChat();
        break;
    case 'getNewMessages':
        getNewMessages();
        break;
    case 'leaveChat':
        leaveChat();
        break;
    case 'getUser':
        getUser();
        break;
    case 'updateUser':
        updateUser();
        break;
}

function authorize($do){
    dlog('do = ' . $do);
        
    $vk_validate = "https://api.vk.com/method/users.get?access_token=";
    $fb_validate = "https://graph.facebook.com/me?access_token=";
    
    $userApi = new Users();
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    $token = mysql_real_escape_string($_POST['token']);
    dlog('$socUserId = '.$socUserId);
    dlog('$socNetId = '.$socNetId);
    dlog('$token = '.$token);
    
    
    
    switch ($socNetId) {
        case 1:
            $url = $vk_validate . $token;
            dlog($url);
            $response = file_get_contents($url);
            
            $json = json_decode($response);
            $data = $json->response;
            $userId = $data[0]->uid;
                        
            $error = $json->error;
            $error_code = $error->error_code;
            
            dlog(" userId = " . $userId);
            dlog(" error_code = " . $error_code);
            
            if($error_code > 0){
                dlog("error");
                return false;
            }
            if($socUserId == $userId){
                dlog("passed for vk");
                return true;
            }
            break;
        case 2:
            $url = $fb_validate . $token;
            dlog($url);
            $response = file_get_contents($url);
            
            $json = json_decode($response);
            $userId = $json->id;
            dlog("id = " . $userId);
            
            if($socUserId == $userId){
                dlog("passed for fb");
                return true;
            }
            break;
    }
    dlog('response from SOCNET = ' . $response);

    return false;
}

function updateUser(){
    $userApi = new Users();
    
    $userId = intval($_POST['userId']);
    $filename = mysql_real_escape_string($_POST['filename']);
    $userName = mysql_real_escape_string($_POST['userName']);
    $userDesc = mysql_real_escape_string($_POST['userDesc']);
    
    $result = $userApi->updateUser($userId, $filename, $userName, $userDesc);
    
    dlog('updateUser');
    dlog($result);
    
    writeResult($result);
}

function getUser(){
    $userApi = new Users();
    
    $userId = intval($_POST['id']);
    
    $result = $userApi->getUserById($userId);
    
    dlog('getUser');
    dlog($result);
    writeResult($result);
}

function leaveChat(){
    $chatApi = new ChatApi();
    $chatId = intval($_POST['chatid']);
    $userId = intval($_POST['userid']);
    
    $result = $chatApi->removeRef($userId, $chatId);
 
    dlog('leaveChat');
    dlog($result);
    
    writeResult($result);    
}

function getNewMessages(){
    $chatId = intval($_POST['chatid']);
    $lastId = intval($_POST['lastid']);
    
    $msgApi = new MsgApi();
    $result = $msgApi->getMessages($chatId, $lastId);
    dlog('getNewMessages');
    dlog($result);
    
    writeResult($result);    
}

function getChat(){
    $chatApi = new ChatApi();
    $chatId = intval($_POST['chatid']);
    
    $jsonResult = new ResultObject();
    dlog('getChat');
    
    $result = $chatApi->getChat($chatId);
    
    writeResult($result);
}

function updateChat(){
    dlog('updateChat');
    $chatApi = new ChatApi();
    $chatId = intval($_POST['chatid']);
    $filename = mysql_real_escape_string($_POST['filename']);
    $chatName = mysql_real_escape_string($_POST['chatName']);
    $chatDesc = mysql_real_escape_string($_POST['chatDesc']);
    $chatCity = mysql_real_escape_string($_POST['chatCity']);
    $chatDate = mysql_real_escape_string($_POST['chatDate']);
    
    $result = $chatApi->updateChat($chatId, $filename, $chatName, $chatDesc, $chatCity, $chatDate);
    dlog('updateChat');
    dlog($result);
    
    writeResult($result);
}

function joinMob(){
    dlog('joinMob');
    $chatId = intval($_POST['chatid']);
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    
    $chatApi = new ChatApi();
    $result = $chatApi->insertRef($userId, $chatId);
    dlog($result);
    
    writeResult($result);
}

function findMobs(){
    $chatApi = new ChatApi();
    $mobName = mysql_real_escape_string($_POST['mobName']);
    $mobDescr = mysql_real_escape_string($_POST['mobDescr']);
    $mobDate = mysql_real_escape_string($_POST['mobDate']);
    $mobCity = mysql_real_escape_string($_POST['mobCity']);
    $useDate = mysql_real_escape_string($_POST['useDate']);
    $userId = mysql_real_escape_string($_POST['userId']);
 
    $result = $chatApi->findChats($mobName, $mobDescr, $mobDate, $mobCity, $useDate, $userId);
    dlog('findMobs');
    dlog($result);
    
    writeResult($result);
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
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    
    $jsonResult = new ResultObject();
    dlog('getChats');
    
    $result = $chatApi->getChats($socUserId, $socNetId);
    
    writeResult($result);
}

function createChat(){
    $chatApi = new ChatApi();
    $socUserId = intval($_POST['socuserid']);
    $socNetId = intval($_POST['socnetid']);
    $chatName = mysql_real_escape_string($_POST['chatName']);
    $chatDescr = mysql_real_escape_string($_POST['chatDescr']);
    $chatDate = mysql_real_escape_string($_POST['chatDate']);
    $chatCity = mysql_real_escape_string($_POST['chatCity']);
    
    dlog($socUserId);
    $userApi = new Users();
    $userId = $userApi->getUserIdBySocNet($socUserId, $socNetId);
    dlog($userId);
    
    $jsonResult = new ResultObject();
    dlog('createChat');
    
    $result = $chatApi->createChat($userId, $chatName, $chatDescr, $chatDate, $chatCity);
    dlog($result);
    if($result > 0){
        dlog('true');
        $result = new Chat($result, $userId, $chatName, $image, $chatDescr, $chatDate, $chatCity);
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

function writeResult($result){
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