$(document).ready(function () {
    // Fetch CSRF token
    $.get("/csrf", function(data) {
        window.csrfToken = data.token;
        window.csrfHeader = data.headerName;
        console.log("CSRF Token retrieved:", window.csrfToken);
    });

    // Menu item click event
    $(".menu-item").click(function(){
        var url = $(this).attr('data-href');
        window.location.href = url;
    });

    // Submenu toggle functionality
    $(".submenu").on("click", function() {
        let submenuIcon = $(this).find(".images");
        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
        $(this).toggleClass("active");
        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
    });
	
	$('#updateForm').on('submit', function(e) {
	      e.preventDefault(); // Prevent default form submission
	      let isValid = true;

	      // Clear previous errors
	      $('.error-message').text('');

	      
	     

	      // Validate Age
	      const name = $('#name').val();
	      if (name === '') {
	          $('#nameError').text('Name is required').show();
			  $('#name').addClass('error-border');
	          isValid = false;
	      } 


	      // Validate Email
	      const email = $('#email').val();
	      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	      if (email === '') {
	          $('#emailError').text('Email is required').show();
			  $('#email').addClass('error-border');
	          isValid = false;
	      } else if (!emailRegex.test(email)) {
	          $('#emailError').text('Please enter a valid email').show();
			  $('#email').addClass('error-border');
	          isValid = false;
	      }

	     
	      if (isValid) {
			let formData = new FormData(this);
			console.log("Form Datas: ",formData);
			console.log("Form Datas showing: ",Array.from(formData.entries()));
			//let aname = formData.get("name");
			//let aemail = formData.get("email");
			$.ajax({
	            url: '/admin/updateAdmin',
	            type: 'POST',
	            data: formData,
	            contentType: false,
	            processData: false,
				xhrFields:{
					withCredentials: true
				},
				headers: { [window.csrfHeader]: window.csrfToken },
	            success: function (response) {
	                toastr.success(response.message);
					if(response.imageUrl){
						$("#profileImg").attr("src",response.imageUrl+"?"+new Date().getTime());
					}
					$("#adminName").text(formData.get("name"));
					//$("#adminEmail").text(formData.get("email"));
					
	            },
	            error: function (xhr) {
	                toastr.error(xhr.responseText || 'Update failed');
	            }
	        });
	      }
	  });
	  $('input').on('focus click', function() {
	      const errorId = $(this).attr('id') + 'Error';
	      $('#' + errorId).hide();
		  //$('#' + errorId).text('');
	      $(this).removeClass('error-border');
	  });
	  $("#image").on("change", function () {
	         const file = this.files[0];
	         if (file) {
	             $("#imagePreview").attr("src", URL.createObjectURL(file));
	         }
	     });
	
});