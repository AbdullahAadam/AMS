$(document).ready(function () {
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
           // Filter students by year
		   let years = new Set();
		       $('.card-container').each(function () {
		           let year = $(this).attr('data-year');
		           if (year !== 'Unknown') {
		               years.add(year);
		           }
		       });

		       years = [...years].sort().reverse(); // Sort in descending order
		       years.forEach(year => {
		           $('#yearFilter').append(`<option value="${year}">${year}</option>`);
		       });

		       // Filter students by year
		       $('#yearFilter').on('change', function () {
		           var selectedYear = $(this).val();
		           $('.card-container').each(function () {
		               var studentYear = $(this).attr('data-year');
		               if (selectedYear === "all" || studentYear === selectedYear) {
		                   $(this).fadeIn();
		               } else {
		                   $(this).fadeOut();
		               }
		           });
		       });
           // Hover effect
       });