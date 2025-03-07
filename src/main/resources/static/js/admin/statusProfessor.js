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
        const profId = $(this).data('profid');  // using the kebab-case attribute
        console.log('Accept button clicked, profId:', profId);

        if (!profId) {
            console.error('Error: Professor ID is undefined');
            toastr.error('Invalid Professor ID');
            return;
        }
        $.ajax({
            url: `/admin/acceptProfessor/${profId}`,
            type: 'PUT', // Using PUT for an update operation
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
            success: function(response) {
				//const profId = $(this).data('profid');
				toastr.success('Professor accepted successfully');
				console.log("success",profId)
				$(`#professor-${profId}`).remove();
				checkIfTableEmpty();
				//$('.table-container').css('display', 'none').fadeIn(0);
				//void document.body.offsetHeight; 
				//document.body.offsetHeight;
				//const $table=$('table');
				//$table.hide().show(0);
				console.log("Row removed"); 
                console.log('Professor accepted:', response);
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
	function checkIfTableEmpty(){
		const rowCount=$('tbody tr').length;
		console.log(rowCount);
		if(rowCount===0){
			$("#noPorfessorMessage").show();
			$("thead").hide();
		}else{
			$("#noPorfessorMessage").hide();
			$("thead").show();
		}
	}
});
