$(document).ready(function(){
	$(".menu-item").click(function(){
		var url=$(this).attr('data-href');
		window.location.href=url;
	});
	checkIfTableEmpty();
	$(".submenu").on("click", function() {
		let submenuIcon = $(this).find(".images");
		$(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
		$(this).toggleClass("active");
		submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
	});
	function checkIfTableEmpty(){						
		const rowCount2=$('#2 tbody tr').length;
		console.log(rowCount2);
		if(rowCount2===0){
			$("#noStudentMessage").show();
			$("#2 thead").hide();
		}else{
			$("#noStudentMessage").hide();
			$("#2 thead").show();
		}
	}
});
	