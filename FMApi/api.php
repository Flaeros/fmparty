<?php
include 'Users.php';

$do = $_POST['do'];
switch ($do) {
    case 'createUser':
        createUser();
        break;
    case 'updateUser':
        updateUser();
        break;
}



function createUser(){
    $userApi = new Users();
    
    $socnet = intval($_POST['socnet']);
    $socuserid = intval($_POST['socuserid']);
    $name = mysql_real_escape_string($_POST['name']);
    
    $user = new User($socnet, $socuserid, $name);
    $result = $userApi->createUser($user);
    
    echo $result;
}

function updateUser(){
    
}