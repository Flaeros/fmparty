<?php
require_once 'Consts.php';

// Where the file is going to be placed 
$target_path = "uploads/";
$new_width = 200;
$new_height = 200;

$server = $_SERVER['DOCUMENT_ROOT'];

$uniqid = uniqid();
$filename = basename( $_FILES['uploadedfile']['name']);
$ext = pathinfo($filename, PATHINFO_EXTENSION);

$unique_file_name = $uniqid . '.' . $ext;

/* Add the original filename to our target path.  
Result is "uploads/filename.extension" */
$target_path = $target_path . $unique_file_name; 

if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
    //$image = exif_thumbnail($target_path, $new_width, $new_height, 'jpeg');
    echo $unique_file_name;
    
    //exec("convert " . $server.$target_path . " -resize 70x70" . $server.$target_path);
    img_resize($target_path, $target_path, $new_height, $new_height);
    
    chmod ($target_path, 0644);
} else{
    echo "There was an error uploading the file, please try again!";
    echo "filename: " .  basename( $_FILES['uploadedfile']['name']);
    echo "target_path: " .$target_path;
}



/***********************************************************************************
Функция img_resize(): генерация thumbnails
Параметры:
  $src             - имя исходного файла
  $dest            - имя генерируемого файла
  $width, $height  - ширина и высота генерируемого изображения, в пикселях
Необязательные параметры:
  $rgb             - цвет фона, по умолчанию - белый
  $quality         - качество генерируемого JPEG, по умолчанию - максимальное (100)
***********************************************************************************/
function img_resize($src, $dest, $width, $height, $rgb=0xFFFFFF, $quality=100)
{
  dlog('img_resize = ');
  if (!file_exists($src)) return false;

  $size = getimagesize($src);
  
  if ($size === false) return false;

  $orig_w = $size[0];
  $orig_h = $size[1];
  
  if($size[0] > $size[1]) {
      $size[0] = $size[1];
      $is_x = true;
      $offset = ($orig_w - $orig_h) / 2;
  }
  else {
      $size[1] = $size[0];
      $is_x = false;
      $offset = ($orig_h - $orig_w) / 2;
  }
  
  // Определяем исходный формат по MIME-информации, предоставленной
  // функцией getimagesize, и выбираем соответствующую формату
  // imagecreatefrom-функцию.
  $format = strtolower(substr($size['mime'], strpos($size['mime'], '/')+1));
  $icfunc = "imagecreatefrom" . $format;
  if (!function_exists($icfunc)) return false;

  $orig_left    = !$is_x  ? 0 : floor($offset);
  $orig_top     = $is_x ? 0 : floor($offset);
  
  $isrc = $icfunc($src);
  $idest = imagecreatetruecolor($width, $height);
  
  imagefill($idest, 0, 0, $rgb);
  imagecopyresampled($idest, $isrc, 0, 0, $orig_left, $orig_top, 
    $width, $height, $size[0], $size[1]);

  imagejpeg($idest, $dest, $quality);

  imagedestroy($isrc);
  imagedestroy($idest);

  return true;

}