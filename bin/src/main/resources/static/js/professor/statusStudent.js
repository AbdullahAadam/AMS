/**
 * 
 */
$(document).ready(function() {
	checkIfTableEmpty();
		$.get("/csrf", function(data) {
			// data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
			window.csrfToken = data.token;
			window.csrfHeader = data.headerName;
			console.log("CSRF Token retrieved:", window.csrfToken);
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
    // Handle Accept button click
    $(document).on('click', '.accept-btn', function() {
        const regNo = $(this).data('regno');  // using the kebab-case attribute
        console.log('Accept button clicked, regNo:', regNo);

        if (!regNo) {
            console.error('Error: Student ID is undefined');
            toastr.error('Invalid Studnet ID');
            return;
        }
        $.ajax({
            url: `/professor/acceptStudent/${regNo}`,
            type: 'PUT', // Using PUT for an update operation
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
            success: function(response) {
				//const profId = $(this).data('profid');
				toastr.success('Student accepted successfully');
				console.log("success",regNo)
				$(`#student-${regNo}`).remove();
				checkIfTableEmpty();
				//$('.table-container').css('display', 'none').fadeIn(0);
				//void document.body.offsetHeight; 
				//document.body.offsetHeight;
				//const $table=$('table');
				//$table.hide().show(0);
				console.log("Row removed"); 
                console.log('Student accepted:', response);
				/*setTimeout(function() {
				    $('.table-container').css('opacity', '0.99'); // slight change
				    setTimeout(function() {
				        $('.table-container').css('opacity', '1');
				    }, 10);
				}, 10);*/
                 
				
            },
            error: function(xhr, status, error) {
                console.error('Error accepting professor:', xhr.responseText || error);
                toastr.error(xhr.responseText || 'Error accepting professor');
            }
        });
    });

    // Handle Reject button click
    $(document).on('click', '.reject-btn', function() {
		console.log($(this).data());
        const regNo = $(this).data('regno');  // using the kebab-case attribute
        console.log('Reject button clicked, regNo:', regNo);

        if (!regNo) {
            console.error('Error: Student ID is undefined');
            toastr.error('Invalid Student ID');
            return;
        }

        $.ajax({
            url: `/professor/rejectStudent/${regNo}`,
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
	function checkIfTableEmpty(){
		const rowCount=$('tbody tr').length;
		console.log(rowCount);
		if(rowCount===0){
			$("#noStudentMessage").show();
			$("thead").hide();
		}else{
			$("#noStudentMessage").hide();
			$("thead").show();
		}
	}
});
