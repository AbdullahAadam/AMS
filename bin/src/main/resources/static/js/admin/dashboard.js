/*function loadContent(){
	var fragment=window.location.hash;
	var contentUrl='';
	if(fragment==='#dashboardContent'){
		contentUrl='/admin/dashboardContent';
	}else if(fragment==='#addProfessor'){
		contentUrl='/admin/addProfessor';
	}else if(fragment==='#editProfessor'){
		contentUrl='/admin/editProfessor';
	}else if(fragment==='#statusProfessor'){
		contentUrl='/admin/statusProfessor';
	}else if(fragment==='#addStudent'){
		contentUrl='/admin/addStudent';
	}else if(fragment==='#editStudent'){
		contentUrl='/admin/editStudent';
	}else if(fragment==='#statusStudent'){
		contentUrl='/admin/statusStudent';
	}
	else if(fragment==='#addDepartment'){
		contentUrl='/admin/addDepartment';
	}
	else if(fragment==='#editDepartment'){
		contentUrl='/admin/editDepartment';
	}
	else if(fragment==='#addSubject'){
		contentUrl='/admin/addSubject';
	}
	else if(fragment==='#editSubject'){
		contentUrl='/admin/editSubject';
	}
	else if(fragment==='#assignProfessor'){
		contentUrl='/admin/assignProfessor';
	}
	else if(fragment==='#semester'){
		contentUrl='/admin/semester';
	}
	else if(fragment==='#holiday'){
		contentUrl='/admin/holiday';
	}
	else{
		contentUrl='/admin/dashboardContent';
	}
	$.ajax({
		url:contentUrl,
		success:function(response){
			$('#content').html(response).fadeIn(200);
			setTimeout(function(){
				if($("#myTable").length){
					if($.fn.dataTable.isDataTable("#myTable")){
						$("#myTable").DataTable().destroy();
					}
					$("#myTable").DataTable();
				}	
			},100);
			
			
		},
		error:function(){
			$('#content').html("<p>Content page not found");
		}
	});
}*/
$(document).ready(function(){
	checkIfTableEmpty();
	$.get("/csrf", function(data) {
		// data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
		window.csrfToken = data.token;
		window.csrfHeader = data.headerName;
		console.log("CSRF Token retrieved:", window.csrfToken);
	});
	
	/*$(".menu-item a").filter(function() {
	       return $(this).text().trim() === "Add Professor";  // Matching exact text
	   }).on("click", function() {
	       toastr.warning("The Professor ID is created automatically.");
	   });
	   if(showToastr==="true"){
	   		//var toastrType='[[${toastrType}]]';
	   		//var toastrMessage='[[${toastrMessage}]]';
	   		switch(toastrType){
	   			case"success":
	   				toastr.success(toastrMessage);
	   				break;
	   			case "error":
	   				toastr.error(toastrMessage);
	   				break;
	   			case "info":
	   				toastr.info(toastrMessage);
	   				break;
	   			case"warning":
	   				toastr.warning(toastrMessage);
	   				break;
	   			default:
	   				console.log("invalid Toastr");
	   		}
	   	   }
	
	/*loadContent();
	$(window).on("hashchange",function(){
		loadContent();
	});
	$(".menu-item a").on("click",function(e){
		e.preventDefault();
		var page=$(this).data("page");
		window.location.hash=page;	
		//loadContent();
	});*/
	$(document).on('click', '#prof', function() {
			console.log($(this).data());
	        const profId = $(this).data('profid');  // using the kebab-case attribute
	        console.log('Reject button clicked, profId:', profId);

	        if (!profId) {
	            console.error('Error: Professor ID is undefined');
	            toastr.error('Invalid Professor ID');
	            return;
	        }

	        $.ajax({
	            url: `/admin/rejectProfessor/${profId}`,
	            type: 'DELETE', // Using DELETE for a deletion operation
				xhrFields:{
					withCredentials: true
				},
				headers: { [window.csrfHeader]: window.csrfToken },
	            success: function(response) {
					console.log("rejected",profId)
	                console.log('Professor rejected:', response);
	                toastr.success('Professor rejected successfully');       
					$(`#professor-${profId}`).remove();
					checkIfTableEmpty();
					//$('.table-container').css('display', 'none').fadeIn(0);
					/*setTimeout(function() {
					    $('.table-container').css('opacity', '0.99'); // slight change
					    setTimeout(function() {
					        $('.table-container').css('opacity', '1');
					    }, 10);
					}, 10);*/
					//void document.body.offsetHeight; 
					//document.body.offsetHeight;
					//const $table=$('table');
					//$table.hide().show(0);
					console.log("Row removed");
	            },
	            error: function(xhr, status, error) {
	                console.error('Error rejecting professor:', xhr.responseText || error);
	                toastr.error(xhr.responseText || 'Error rejecting professor');
	            }
	        });
	   });
	   $(document).on('click','#stud', function() {
	   		console.log($(this).data());
	           const regNo = $(this).data('regno');  
	           console.log('Reject button clicked, regNo:', regNo);

	           if (!regNo) {
	               console.error('Error: Student ID is undefined');
	               toastr.error('Invalid Student ID');
	               return;
	           }

	           $.ajax({
	               url: `/admin/rejectStudent/${regNo}`,
	               type: 'DELETE', // Using DELETE for a deletion operation
	   			xhrFields:{
	   				withCredentials: true
	   			},
	   			headers: { [window.csrfHeader]: window.csrfToken },
	               success: function(response) {
	   				console.log("rejected",regNo)
	                   console.log('Student rejected:', response);
	                   toastr.success('Student rejected successfully');       
	   				$(`#student-${regNo}`).remove();
	   				checkIfTableEmpty();
	   				//$('.table-container').css('display', 'none').fadeIn(0);
	   				/*setTimeout(function() {
	   				    $('.table-container').css('opacity', '0.99'); // slight change
	   				    setTimeout(function() {
	   				        $('.table-container').css('opacity', '1');
	   				    }, 10);
	   				}, 10);*/
	   				//void document.body.offsetHeight; 
	   				//document.body.offsetHeight;
	   				//const $table=$('table');
	   				//$table.hide().show(0);
	   				console.log("Row removed");
	               },
	               error: function(xhr, status, error) {
	                   console.error('Error rejecting Student:', xhr.responseText || error);
	                   toastr.error(xhr.responseText || 'Error rejecting Student');
	               }
	           });
	});
	$(".menu-item").click(function(){
		var url=$(this).attr('data-href');
		window.location.href=url;
	});
	$(".submenu").on("click", function() {
	        let submenuIcon = $(this).find(".images");
	        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
	        $(this).toggleClass("active");
	        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
	    });

	    // Toastr notifications for specific submenu items
	    $(".menu-item a:contains(' Add Professor')").on("click", function() {
	        toastr.warning("The Professor ID is created automatically.");
	    });
	    $(".menu-item a:contains('Add Department')").on("click", function() {
	        toastr.warning("Department Code is created automatically.");
	    });
	    $(".menu-item a:contains('Add Subject')").on("click", function() {
	        toastr.info("Subject ID is created automatically. You can change it if you want.");
	    });
		function checkIfTableEmpty(){
					const rowCount=$('#1 tbody tr').length;
					const rowCount2=$('#2 tbody tr').length;
					console.log(rowCount);
					const data=$('#1 tbody tr').text();
					console.log(data);
					if(rowCount===0){
						$("#noPorfessorMessage").show();
						$("#1 thead").hide();
					}else{
						$("#noPorfessorMessage").hide();
						$("#1 thead").show();
					}
					if(rowCount2===0){
						$("#noStudentMessage").show();
						$("#2 thead").hide();
					}else{
						$("#noStudentMessage").hide();
						$("#2 thead").show();
					}
				}
});































































/*$(document).ready(function() {
    // Variable to track if the content has been loaded at least once
			  loadContentFromHash();

	           // Listen for hash changes (when user clicks on a link)
	           $(window).on('hashchange', function() {
	               loadContentFromHash();
	           });

	           // Function to load content based on the hash or default to dashboardContent if no hash
	           function loadContentFromHash() {
	               var hash = window.location.hash.substring(1); // Get hash part from URL
	               
	               // If no hash is present, default to 'dashboardContent'
	               if (!hash) {
	                   hash = 'dashboardContent';
	               }

	               // Load the corresponding content based on the hash
	               loadContent(hash);
	           }

	           // Function to dynamically load content into #content based on the page name
	           function loadContent(page) {
	               $("#content").fadeOut(200, function() {
	                   $("#content").load("/admin/" + page, function(response, status, xhr) {
	                       if (status == "error") {
	                           $("#content").html("<h2>Error loading content: " + xhr.status + " " + xhr.statusText + "</h2>");
	                       }
	                       $("#content").fadeIn(200);
	                   });
	               });
	           }

	           // Handle clicks on menu items to change hash and load content dynamically
	           $(".menu-item").on("click", function() {
	               var page = $(this).data("page");
	               window.location.hash = page; // Update URL hash
	           });

    // Submenu Toggle logic
    $(".submenu").on("click", function() {
        let submenuIcon = $(this).find(".images");
        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
        $(this).toggleClass("active");
        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
    });

    // Toastr notifications for specific submenu items
    $(".submenu-items li:contains('Add Professor')").on("click", function() {
        toastr.warning("The Professor ID is created automatically.");
    });
    $(".submenu-items li:contains('Add Department')").on("click", function() {
        toastr.warning("Department Code is created automatically.");
    });
    $(".submenu-items li:contains('Add Subject')").on("click", function() {
        toastr.info("Subject ID is created automatically. You can change it if you want.");
    });

});*/


