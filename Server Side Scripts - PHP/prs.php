<?php

$script='

<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" href="/favicon.ico" type="image/png">
<link rel="stylesheet" href="/w3.css">

<style>

.no-js #loader { display: none;  }
.js #loader { display: block; position: absolute; left: 100px; top: 0; }
.se-pre-con {
	position: fixed;
	left: 0px;
	top: 0px;
	width: 100%;
	height: 100%;
	z-index: 9999;
	background: url(/loader.gif) center no-repeat #fff;
}


</style>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.js"></script>

<script>
	$(window).load(function() {
		// Animate loader off screen
		$(".se-pre-con").fadeOut("slow");;
	});
</script>

<script src="https://www.gstatic.com/firebasejs/5.8.2/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/5.8.2/firebase-firestore.js"></script>
<script>
  var config = {

    apiKey: "key",
    authDomain: "yours.firebaseapp.com",
    databaseURL: "yours.firebaseio.com",
    projectId: "your-id",
    storageBucket: "your-prj.appspot.com",
    messagingSenderId: "00000"

  };
  firebase.initializeApp(config);

var db = firebase.firestore();



var docRef = db.collection("users").doc("'.$id.'");


docRef.get().then(function(doc) {
    if (doc.exists) {
        
       let str=JSON.stringify(doc.get("webidesi"));
       let str2=JSON.parse(str);

     //  let countStr=JSON.stringify(str2["'.$name.'"]);

let countStr=String(str2["'.$name.'"]);

      let str3=countStr.substring(countStr.lastIndexOf(",")+1,countStr.indexOf("+"));

var counter = parseInt(str3);

counter++;



var docR= db.collection("users").doc("'.$id.'");

var setWithMerge = docR.set({
    
 webidesi: {
        '.$name.': countStr.substring(0,countStr.lastIndexOf(","))+","+counter+"+"
        
    }




}, { merge: true }).then(function(doc) {

window.location.href =  "'.$url.'";

 });






    } else {
        // doc.data() will be undefined in this case
        console.log("No such document!");
    }
}).catch(function(error) {
    console.log("Error getting document:", error);
});
</script>
<div class="se-pre-con"></div>

';

?>

